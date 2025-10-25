package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.data.model.User;
import com.gymapp.gym_backend_service.data.enums.UserRole;
import com.gymapp.gym_backend_service.repository.UserRepository;
import com.gymapp.gym_backend_service.service.AdminService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService service;

    @PostMapping
    public ResponseEntity<User> createAdmin(@RequestBody User user) {
        return ResponseEntity.ok(service.createAdmin(user));
    }
}