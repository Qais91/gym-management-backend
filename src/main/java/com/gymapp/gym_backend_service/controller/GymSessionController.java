package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.authorization.JWTHandler;
import com.gymapp.gym_backend_service.data.model.GymSession;
import com.gymapp.gym_backend_service.data.model.Member;
import com.gymapp.gym_backend_service.data.model.Trainer;
import com.gymapp.gym_backend_service.data.model.User;
import com.gymapp.gym_backend_service.data.dto.request.GymSessionRequest;
import com.gymapp.gym_backend_service.data.dto.response.ApiResponse;
import com.gymapp.gym_backend_service.data.dto.response.gym_session.CreateGymSessionResponseDTO;
import com.gymapp.gym_backend_service.data.dto.response.gym_session.GymSessionFullResponseDTO;
import com.gymapp.gym_backend_service.data.dto.response.gym_session.SessionResponseDTO;
import com.gymapp.gym_backend_service.data.enums.ActivityType;
import com.gymapp.gym_backend_service.data.enums.UserRole;
import com.gymapp.gym_backend_service.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @Autowired
    private JWTHandler jwtHandler;

    Long getMemberID(String header) {
        String token = header.substring(7);
        return jwtHandler.extractUserId(token);
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping
    public ResponseEntity<?> getAllSessions() {
        List<GymSession> allSessions = gymSessionRepository.findAll();
        if(allSessions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", "No session so far"));
        }
        return ResponseEntity.ok(allSessions.stream().map((session -> new SessionResponseDTO(session))).toList());
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getSessionByUser(@PathVariable("id") Long id) {
        List<GymSession> sessionFilteredByMember = gymSessionRepository.findByMember((Member) userRepository.findById(id).get());
        if(sessionFilteredByMember.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", "Enter a valid member ID"));
        }

        return ResponseEntity.ok(sessionFilteredByMember.stream().map((gymSession -> new SessionResponseDTO(gymSession))).toList());
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/trainer/{id}")
    public ResponseEntity<?> getSessionByTrainer(@PathVariable("id") Long id) {
        List<GymSession> sessionFilteredByTrainer = gymSessionRepository.findByTrainer((Trainer) userRepository.findById(id).get());
        if(sessionFilteredByTrainer.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", "Enter a valid Trainer ID"));
        }

        return ResponseEntity.ok(sessionFilteredByTrainer.stream().map((gymSession -> new SessionResponseDTO(gymSession))).toList());
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getSessionById(@PathVariable("id") Long id) {
         Optional<GymSession> gymSession = gymSessionRepository.findById(id);
         if(gymSession.isEmpty()) { ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", "Enter a valid Session ID")); }
        return ResponseEntity.ok(new GymSessionFullResponseDTO(gymSession.get()));
    }

    @PreAuthorize("hasRole('MEMBER')")
    @PostMapping
    public ResponseEntity<?> createGymSession(@RequestHeader("Authorization") String header, @RequestBody GymSessionRequest request) {
        Long memberID = getMemberID(header);
        if(memberID == null) { return ResponseEntity.badRequest().body(new ApiResponse("error", "Invalid token. Kindly check token")); }

        Optional<User> trainerOpt = userRepository.findById(request.getTrainerId());
        if (trainerOpt.isEmpty() || !UserRole.TRAINER.equals(trainerOpt.get().getUserRole())) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", "Invalid trainer ID or user is not a trainer."));
        }
        Optional<User> memberOpt = userRepository.findById(memberID);
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
