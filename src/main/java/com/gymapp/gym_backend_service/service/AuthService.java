package com.gymapp.gym_backend_service.service;

import com.gymapp.gym_backend_service.authorization.JWTHandler;
import com.gymapp.gym_backend_service.data.dto.response.ApiResponse;
import com.gymapp.gym_backend_service.data.dto.response.auth.LoginResponse;
import com.gymapp.gym_backend_service.data.model.User;
import com.gymapp.gym_backend_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private JWTHandler jwtService;

    public ResponseEntity<?> authenticateUser(User loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername());
        if (user != null) {

            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            String token = jwtService.generateToken((UserDetails) auth.getPrincipal(), user);
            String role = user.getUserRole().name();

            return ResponseEntity.ok(new LoginResponse(user.getUsername(), role, token));

        } else {
            return ResponseEntity.status(401).body(new ApiResponse("error", "Invalid credentials"));
        }
    }
}
