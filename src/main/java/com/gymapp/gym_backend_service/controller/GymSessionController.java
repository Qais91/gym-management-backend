package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.model.GymSession;
import com.gymapp.gym_backend_service.model.Member;
import com.gymapp.gym_backend_service.model.Trainer;
import com.gymapp.gym_backend_service.model.User;
import com.gymapp.gym_backend_service.model.dto.request.AddSessionLogRequest;
import com.gymapp.gym_backend_service.model.dto.request.CreateGymSessionRequest;
import com.gymapp.gym_backend_service.model.dto.request.GymSessionRequest;
import com.gymapp.gym_backend_service.model.dto.response.GymSessionResponse;
import com.gymapp.gym_backend_service.model.dto.response.SessionLogResponse;
import com.gymapp.gym_backend_service.model.enums.ActivityType;
import com.gymapp.gym_backend_service.model.enums.UserRole;
import com.gymapp.gym_backend_service.repository.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/gym-sessions")
public class GymSessionController {

    private final GymSessionRepository gymSessionRepository;
    private final UserRepository userRepository;

    public GymSessionController(GymSessionRepository gymSessionRepository, UserRepository userRepository) {
        this.gymSessionRepository = gymSessionRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSessionById(@PathVariable Long id) {
        return gymSessionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createGymSession(@RequestBody GymSessionRequest request) {

        Optional<User> trainerOpt = userRepository.findById(request.getTrainerId());
        if (trainerOpt.isEmpty() || !UserRole.Trainer.equals(trainerOpt.get().getUserRole())) {
            return ResponseEntity.badRequest().body("Invalid trainer ID or user is not a trainer.");
        }
        Optional<User> memberOpt = userRepository.findById(request.getTrainerId());
        if (memberOpt.isEmpty() || !UserRole.GymMemeber.equals(memberOpt.get().getUserRole())) {
            return ResponseEntity.badRequest().body("Invalid member ID or user is not a member.");
        }

        GymSession session = new GymSession();
        session.setTrainer((Trainer) trainerOpt.get());
        session.setMember((Member) memberOpt.get());
        session.setNotes(request.getNotes());
        session.setActivityType(request.getActivityType());
        session.setStartTime(request.getStartTime());
        session.setEndTime(request.getEndTime());

        GymSession savedSession = gymSessionRepository.save(session);
        return ResponseEntity.ok(savedSession);
    }

}
