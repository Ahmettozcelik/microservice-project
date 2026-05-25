package com.microservices.auth_service.service;

import com.microservices.auth_service.dto.AuthResponse;
import com.microservices.auth_service.dto.LoginRequest;
import com.microservices.auth_service.dto.RegisterRequest;
import com.microservices.auth_service.model.User;
import com.microservices.auth_service.model.Role;
import com.microservices.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole(Role.valueOf(request.getRole().toUpperCase()));
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        return "User registered successfully";
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = "TEMP_TOKEN"; //

        return new AuthResponse(token, user.getRole().name());
    }
}