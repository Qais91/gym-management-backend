package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.data.model.Trainer;
import com.gymapp.gym_backend_service.data.model.User;
import com.gymapp.gym_backend_service.data.dto.request.trainer.CreateTrainerRequestDTO;
import com.gymapp.gym_backend_service.data.dto.response.ApiResponse;
import com.gymapp.gym_backend_service.data.dto.response.UserResponse;
import com.gymapp.gym_backend_service.data.enums.UserRole;
import com.gymapp.gym_backend_service.repository.TrainerRepository;
import com.gymapp.gym_backend_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainer")
public class TrainerController {

    @Autowired
    private TrainerRepository trainerRepository;
    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> addTrainer(@RequestBody CreateTrainerRequestDTO requestDTO) {
        if (userRepository.existsByUsername(requestDTO.getUsername())) {
            return ResponseEntity.badRequest().body(new ApiResponse("false", "Username already exists"));
        }
        if(userRepository.existsByEmail(requestDTO.getEmail())) {
            return ResponseEntity.badRequest().body(new ApiResponse("false", "Email already exists"));
        }
        if(userRepository.existsByPhoneNumber(requestDTO.getPhoneNumber())) {
            return ResponseEntity.badRequest().body(new ApiResponse("false", "Number already exists"));
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Trainer trainer = new Trainer(requestDTO);
        trainer.setPassword(encoder.encode(requestDTO.getPassword()));

        return ResponseEntity.ok(trainerRepository.save(trainer));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER', 'MEMBER')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getTrainerById(@PathVariable(value = "id") Long id) {
        List<User> trainer = userRepository.findByUserRole(UserRole.TRAINER);
        if(trainer.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("error", "No Members"));
        }
        return ResponseEntity.ok(
                trainer.stream()
                        .map((user) -> new UserResponse(user))
                        .toList()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER', 'MEMBER')")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllTrainer() {
        return ResponseEntity.ok(
            userRepository.findByUserRole(UserRole.TRAINER)
                .stream()
                .map((user) -> new UserResponse(user))
                .toList()
        );
    }
}
