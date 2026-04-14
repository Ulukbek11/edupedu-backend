package com.edupedu.app.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edupedu.app.model.User;
import com.edupedu.app.request.AdminRegistrationRequest;
import com.edupedu.app.request.AuthenticationRequest;
import com.edupedu.app.request.RefreshRequest;
import com.edupedu.app.request.RegistrationRequest;
import com.edupedu.app.response.AdminRegistrationResponse;
import com.edupedu.app.response.AuthenticationResponse;
import com.edupedu.app.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/auth/register")
    public ResponseEntity<AdminRegistrationResponse<User, ?>> register(@RequestBody @Valid RegistrationRequest request) {
        return new ResponseEntity<>(authService.register(request), HttpStatus.OK);
    }

    @PostMapping("/admin/registerNewUser")
    public ResponseEntity<AdminRegistrationResponse<User, ?>> adminRegistration(
            @RequestBody
            @Valid
            AdminRegistrationRequest request) {
        return new ResponseEntity<>(authService.adminRegistration(request), HttpStatus.OK);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody
            @Valid
            AuthenticationRequest request) {
        return new ResponseEntity<>(authService.login(request), HttpStatus.OK);
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(
            @RequestBody
            @Valid
            RefreshRequest request) {
        return new ResponseEntity<>(authService.refreshToken(request), HttpStatus.OK);
    }

    @PostMapping("/auth/forgot-password")
    public ResponseEntity<Map<String, String>> processForgotPassword(@RequestParam String email) {
        return new ResponseEntity<>(authService.processForgotPassword(email), HttpStatus.OK);
    }

    @PostMapping("/auth/reset-password")
    public ResponseEntity<Map<String, String>> processResetPassword(
        @RequestParam String token,
        @RequestParam String password,
        @RequestParam String confirmPassword
        // RedirectAttributes redirectAttributes
    ) {

        return new ResponseEntity<>(authService.createPasswordResetTokenForUser(token, password, confirmPassword), HttpStatus.OK);

    }


}
