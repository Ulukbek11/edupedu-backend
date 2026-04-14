package com.edupedu.app.security;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.edupedu.app.model.RefreshToken;
import com.edupedu.app.model.User;
import com.edupedu.app.repository.RefreshTokenRepository;
import com.edupedu.app.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public String issueRefreshToken(final User user) {
        final String refreshToken = this.jwtService.generateRefreshToken(user.getUsername());
        saveRefreshToken(user, refreshToken);
        return refreshToken;
    }

    @Transactional
    public RotatedTokens rotateRefreshToken(final String rawRefreshToken) {
        final String username = extractUsername(rawRefreshToken);
        if (!isRefreshTokenValid(rawRefreshToken, username)) {
            throw invalidRefreshToken();
        }

        final User user = this.userRepository.findByEmailIgnoreCase(username)
                                                                              .orElseThrow(this::invalidRefreshToken);
        final String tokenId = extractTokenId(rawRefreshToken);
        final RefreshToken storedToken = this.refreshTokenRepository.findByTokenId(tokenId)
                                                                   .orElseThrow(this::invalidRefreshToken);

        if (!storedToken.getUser().getId().equals(user.getId())) {
            revokeToken(storedToken, null);
            throw invalidRefreshToken();
        }

        if (storedToken.isRevoked()) {
            revokeAllRefreshTokensForUser(user.getId());
            throw invalidRefreshToken();
        }

        if (storedToken.getExpiresAt().isBefore(Instant.now()) || !isUserEligible(user)) {
            revokeAllRefreshTokensForUser(user.getId());
            throw invalidRefreshToken();
        }

        final String newAccessToken = this.jwtService.generateAccessToken(user.getUsername());
        final String newRefreshToken = this.jwtService.generateRefreshToken(user.getUsername());
        final String replacementTokenId = this.jwtService.extractTokenId(newRefreshToken);

        revokeToken(storedToken, replacementTokenId);
        saveRefreshToken(user, newRefreshToken);

        return new RotatedTokens(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void revokeAllRefreshTokensForUser(final Long userId) {
        final Instant revokedAt = Instant.now();
        this.refreshTokenRepository.findAllByUserIdAndRevokedFalse(userId)
                                   .forEach(token -> revokeToken(token, null, revokedAt));
    }

    private void saveRefreshToken(final User user, final String refreshToken) {
        this.refreshTokenRepository.save(RefreshToken.builder()
                                                     .tokenId(this.jwtService.extractTokenId(refreshToken))
                                                     .user(user)
                                                     .expiresAt(this.jwtService.extractExpiration(refreshToken).toInstant())
                                                     .revoked(false)
                                                     .build());
    }

    private void revokeToken(final RefreshToken refreshToken, final String replacedByTokenId) {
        revokeToken(refreshToken, replacedByTokenId, Instant.now());
    }

    private void revokeToken(final RefreshToken refreshToken,
                             final String replacedByTokenId,
                             final Instant revokedAt) {
        refreshToken.setRevoked(true);
        refreshToken.setRevokedAt(revokedAt);
        refreshToken.setReplacedByTokenId(replacedByTokenId);
    }

    private boolean isUserEligible(final User user) {
        return user.isEnabled()
                && user.isAccountNonExpired()
                && user.isAccountNonLocked()
                && user.isCredentialsNonExpired();
    }

    private String extractUsername(final String refreshToken) {
        try {
            return this.jwtService.extractUsername(refreshToken);
        } catch (final RuntimeException e) {
            throw invalidRefreshToken();
        }
    }

    private String extractTokenId(final String refreshToken) {
        try {
            return this.jwtService.extractTokenId(refreshToken);
        } catch (final RuntimeException e) {
            throw invalidRefreshToken();
        }
    }

    private boolean isRefreshTokenValid(final String refreshToken, final String username) {
        try {
            return this.jwtService.validateToken(refreshToken, username, JwtService.TOKEN_TYPE_REFRESH);
        } catch (final RuntimeException e) {
            throw invalidRefreshToken();
        }
    }


    private ResponseStatusException invalidRefreshToken() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
    }

    public record RotatedTokens(String accessToken, String refreshToken) {}
}
