package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.model.Member;
import com.gymapp.gym_backend_service.model.Trainer;
import com.gymapp.gym_backend_service.model.User;
import com.gymapp.gym_backend_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername());

        if (user != null && user.getPassword().equals(loginRequest.getPassword())) {
            String role;

            if (user instanceof Member) {
                role = "customer";
            } else if (user instanceof Trainer) {
                role = "trainer";
            } else {
                role = "guest";
            }

            return ResponseEntity.ok(new LoginResponse(user.getId(), user.getUsername(), role));
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    public static class LoginResponse {
        private Long userId;
        private String username;
        private String role;

        public LoginResponse(Long userId, String username, String role) {
            this.userId = userId;
            this.username = username;
            this.role = role;
        }

        public Long getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getRole() { return role; }
    }

}
