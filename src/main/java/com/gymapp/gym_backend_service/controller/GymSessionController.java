package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.model.GymSession;
import com.gymapp.gym_backend_service.model.Member;
import com.gymapp.gym_backend_service.model.Trainer;
import com.gymapp.gym_backend_service.model.User;
import com.gymapp.gym_backend_service.model.dto.request.GymSessionRequest;
import com.gymapp.gym_backend_service.model.dto.response.ApiResponse;
import com.gymapp.gym_backend_service.model.dto.response.gym_session.CreateGymSessionResponseDTO;
import com.gymapp.gym_backend_service.model.dto.response.gym_session.GymSessionFullResponseDTO;
import com.gymapp.gym_backend_service.model.dto.response.gym_session.SessionResponseDTO;
import com.gymapp.gym_backend_service.model.enums.ActivityType;
import com.gymapp.gym_backend_service.model.enums.UserRole;
import com.gymapp.gym_backend_service.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/gym-sessions")
public class GymSessionController {

    @Autowired
    private GymSessionRepository gymSessionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MemberRepository memberRepository;

    @GetMapping
    public ResponseEntity<?> getAllSessions() {
        List<GymSession> allSessions = gymSessionRepository.findAll();
        if(allSessions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", "No session so far"));
        }
        return ResponseEntity.ok(allSessions.stream().map((session -> new SessionResponseDTO(session))).toList());
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getSessionByUser(@PathVariable Long id) {
        List<GymSession> sessionFilteredByMember = gymSessionRepository.findByMember((Member) userRepository.findById(id).get());
        if(sessionFilteredByMember.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", "Enter a valid member ID"));
        }

        return ResponseEntity.ok(sessionFilteredByMember.stream().map((gymSession -> new SessionResponseDTO(gymSession))).toList());
    }

    @GetMapping("/trainer/{id}")
    public ResponseEntity<?> getSessionByTrainer(@PathVariable Long id) {
        List<GymSession> sessionFilteredByTrainer = gymSessionRepository.findByTrainer((Trainer) userRepository.findById(id).get());
        if(sessionFilteredByTrainer.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", "Enter a valid Trainer ID"));
        }

        return ResponseEntity.ok(sessionFilteredByTrainer.stream().map((gymSession -> new SessionResponseDTO(gymSession))).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSessionById(@PathVariable Long id) {
         Optional<GymSession> gymSession = gymSessionRepository.findById(id);
         if(gymSession.isEmpty()) { ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", "Enter a valid Session ID")); }
        return ResponseEntity.ok(new GymSessionFullResponseDTO(gymSession.get()));
    }

    @PostMapping
    public ResponseEntity<?> createGymSession(@RequestBody GymSessionRequest request) {

        Optional<User> trainerOpt = userRepository.findById(request.getTrainerId());
        if (trainerOpt.isEmpty() || !UserRole.TRAINER.equals(trainerOpt.get().getUserRole())) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", "Invalid trainer ID or user is not a trainer."));
        }
        Optional<User> memberOpt = userRepository.findById(request.getMemberId());
        if (memberOpt.isEmpty() || !UserRole.MEMBER.equals(memberOpt.get().getUserRole())) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", "Invalid member ID or user is not a member."));
        }

        if (!memberRepository.findById(memberOpt.get().getId()).get().getTrainer().getId().equals(trainerOpt.get().getId())) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", "Invalid trainer is been assigned"));
        }

        ActivityType activityType;
        try {
            activityType = ActivityType.valueOf(request.getActivityType().toUpperCase());
        } catch (Exception e) {
             String allowedActivity = String.join(", ",  Arrays.stream(ActivityType.values()).map(Enum::name).toList());
            return ResponseEntity.badRequest().body(new ApiResponse("error", "Invalid Activity Type. Allowed values are: " + allowedActivity));
        }

        GymSession session = new GymSession();
        session.setTrainer((Trainer) trainerOpt.get());
        session.setMember((Member) memberOpt.get());
        session.setNotes(request.getNotes());
        session.setActivityType(activityType);
        session.setStartTime(request.getStartTime());
        session.setEndTime(request.getEndTime());

        GymSession savedSession = gymSessionRepository.save(session);
        return ResponseEntity.ok(new CreateGymSessionResponseDTO(savedSession));
    }

}
