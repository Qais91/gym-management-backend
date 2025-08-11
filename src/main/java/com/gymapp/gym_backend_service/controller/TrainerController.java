package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.model.Trainer;
import com.gymapp.gym_backend_service.model.User;
import com.gymapp.gym_backend_service.model.dto.response.ApiResponse;
import com.gymapp.gym_backend_service.model.dto.response.UserResponse;
import com.gymapp.gym_backend_service.model.enums.UserRole;
import com.gymapp.gym_backend_service.repository.TrainerRepository;
import com.gymapp.gym_backend_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainer")
public class TrainerController {

    @Autowired
    private TrainerRepository trainerRepository;
    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> addTrainer(@RequestBody Trainer trainer) {
        if (userRepository.existsByUsername(trainer.getUsername())) {
            return ResponseEntity.badRequest().body(new ApiResponse("false", "Username already exists"));
        }
        if(userRepository.existsByEmail(trainer.getEmail())) {
            return ResponseEntity.badRequest().body(new ApiResponse("false", "Email already exists"));
        }
        if(userRepository.existsByPhoneNumber(trainer.getPhoneNumber())) {
            return ResponseEntity.badRequest().body(new ApiResponse("false", "Number already exists"));
        }
        return ResponseEntity.ok(trainerRepository.save(trainer));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTrainerById(@PathVariable Long id) {
        List<User> trainer = userRepository.findByUserRole(UserRole.Trainer);
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

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllTrainer() {
        return ResponseEntity.ok(
            userRepository.findByUserRole(UserRole.Trainer)
                .stream()
                .map((user) -> new UserResponse(user))
                .toList()
        );
    }
}
