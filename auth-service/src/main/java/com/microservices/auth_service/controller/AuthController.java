package com.microservices.auth_service.controller;

import com.microservices.auth_service.dto.AuthResponse;
import com.microservices.auth_service.dto.LoginRequest;
import com.microservices.auth_service.dto.RegisterRequest;
import com.microservices.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}