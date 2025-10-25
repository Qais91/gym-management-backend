package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.data.dto.response.trainer.TrainerInfoResponseDTO;
import com.gymapp.gym_backend_service.data.enums.ActivityType;
import com.gymapp.gym_backend_service.data.model.Trainer;
import com.gymapp.gym_backend_service.data.dto.request.trainer.CreateTrainerRequestDTO;
import com.gymapp.gym_backend_service.data.dto.response.ApiResponse;
import com.gymapp.gym_backend_service.data.dto.response.UserResponse;
import com.gymapp.gym_backend_service.data.enums.UserRole;
import com.gymapp.gym_backend_service.repository.TrainerRepository;
import com.gymapp.gym_backend_service.repository.UserRepository;
import com.gymapp.gym_backend_service.service.TrainerService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/trainer")
public class TrainerController {

    @Autowired
    private TrainerService service;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> addTrainer(@Valid @RequestBody CreateTrainerRequestDTO requestDTO) {
        try {
            return ResponseEntity.ok(new TrainerInfoResponseDTO(service.createTrainer(requestDTO)));
        } catch (Exception e) {
            String err_msg = e.getMessage();
            return ResponseEntity.badRequest().body(new ApiResponse("false", err_msg));
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER', 'MEMBER')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getTrainerById(@PathVariable(value = "id") Long id) {
        try {
            return ResponseEntity.ok(new TrainerInfoResponseDTO(service.getTrainerById(id)));
        } catch (EntityNotFoundException e) {
            String err_msg = e.getMessage();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", err_msg));
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER', 'MEMBER')")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllTrainer() {
        return ResponseEntity.ok(service.getAllTrainer());
    }
}
