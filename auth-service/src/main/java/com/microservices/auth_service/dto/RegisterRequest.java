package com.microservices.auth_service.dto;

import lombok.Data;

@Data
public class RegisterRequest {

    private String name;
    private String email;
    private String password;
    private String phone;

    private String role; // CUSTOMER / COURIER / ADMIN
}