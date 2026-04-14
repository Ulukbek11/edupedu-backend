package com.edupedu.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edupedu.app.model.RefreshToken;
import com.edupedu.app.model.University;
import com.edupedu.app.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByResetToken(String token);

    Optional<User> findByEmailIgnoreCase(String username);
    List<User> findAllByUniversityId(Long id);
}