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
    private TrainerRepository trainerRepository;
    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> addTrainer(@Valid @RequestBody CreateTrainerRequestDTO requestDTO) {
        if (userRepository.existsByUsername(requestDTO.getUsername())) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", "Username already exists"));
        }
        if(userRepository.existsByEmail(requestDTO.getEmail())) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", "Email already exists"));
        }
        if(userRepository.existsByPhoneNumber(requestDTO.getPhoneNumber())) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", "Number already exists"));
        }

        ActivityType activityType;
        try {
            activityType = ActivityType.valueOf(requestDTO.getSpecialization().toUpperCase());
        } catch (Exception e) {
            String allowedActivity = String.join(", ",  Arrays.stream(ActivityType.values()).map(Enum::name).toList());
            return ResponseEntity.badRequest().body(new ApiResponse("error", "Invalid Specialization Type. Allowed values are: " + allowedActivity));
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Trainer trainer = new Trainer(requestDTO);
        trainer.setPassword(encoder.encode(requestDTO.getPassword()));
        trainer.setSpecialization(activityType.toString());

        return ResponseEntity.ok(new TrainerInfoResponseDTO(trainerRepository.save(trainer)));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER', 'MEMBER')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getTrainerById(@PathVariable(value = "id") Long id) {
        Optional<Trainer> trainer = trainerRepository.findById(id);
        if(trainer.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("error", "No Trainer associated with this ID"));
        }

        return ResponseEntity.ok(new TrainerInfoResponseDTO(trainer.get()));
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
