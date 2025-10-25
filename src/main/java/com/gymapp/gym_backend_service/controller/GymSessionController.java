package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.authorization.JWTHandler;
import com.gymapp.gym_backend_service.data.model.*;
import com.gymapp.gym_backend_service.data.dto.request.GymSessionRequest;
import com.gymapp.gym_backend_service.data.dto.response.ApiResponse;
import com.gymapp.gym_backend_service.data.dto.response.gym_session.CreateGymSessionResponseDTO;
import com.gymapp.gym_backend_service.data.dto.response.gym_session.GymSessionFullResponseDTO;
import com.gymapp.gym_backend_service.data.dto.response.gym_session.SessionResponseDTO;
import com.gymapp.gym_backend_service.data.enums.ActivityType;
import com.gymapp.gym_backend_service.data.enums.UserRole;
import com.gymapp.gym_backend_service.repository.*;
import com.gymapp.gym_backend_service.service.GymSessionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/gym-sessions")
public class GymSessionController {

    @Autowired
    private GymSessionService service;

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping
    public ResponseEntity<?> getAllSessions(@RequestHeader("Authorization") String header) {
        try {
            return ResponseEntity.ok(service.getAllSessionsByUser(header));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/trainer/{id}")
    public ResponseEntity<?> getSessionByTrainer(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(service.getAllSessionByTrainer(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getSessionById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(new GymSessionFullResponseDTO(service.getSessionByID(id)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('MEMBER')")
    @PostMapping
    public ResponseEntity<?> createGymSession(@RequestHeader("Authorization") String header, @Valid @RequestBody GymSessionRequest request) {
        try {
            return ResponseEntity.ok(new CreateGymSessionResponseDTO(service.createSession(header, request)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", e.getMessage()));
        }
    }

}
