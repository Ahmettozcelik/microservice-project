package com.microservices.auth_service.model;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String id;

    private String name;

    private String email;

    private String password;

    private String phone;

    private Role role;

    private LocalDateTime createdAt;
}