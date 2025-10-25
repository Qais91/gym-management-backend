package com.gymapp.gym_backend_service.service;

import com.gymapp.gym_backend_service.data.dto.request.trainer.CreateTrainerRequestDTO;
import com.gymapp.gym_backend_service.data.dto.response.ApiResponse;
import com.gymapp.gym_backend_service.data.dto.response.UserResponse;
import com.gymapp.gym_backend_service.data.dto.response.trainer.TrainerInfoResponseDTO;
import com.gymapp.gym_backend_service.data.enums.ActivityType;
import com.gymapp.gym_backend_service.data.enums.UserRole;
import com.gymapp.gym_backend_service.data.model.Trainer;
import com.gymapp.gym_backend_service.repository.TrainerRepository;
import com.gymapp.gym_backend_service.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class TrainerService {

    @Autowired
    private TrainerRepository trainerRepository;
    @Autowired
    private UserRepository userRepository;

    public Trainer createTrainer(CreateTrainerRequestDTO requestDTO) {
        if (userRepository.existsByUsername(requestDTO.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if(userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if(userRepository.existsByPhoneNumber(requestDTO.getPhoneNumber())) {
            throw new IllegalArgumentException("Number already exists");
        }

        ActivityType activityType;
        try {
            activityType = ActivityType.valueOf(requestDTO.getSpecialization().toUpperCase());
        } catch (Exception e) {
            String allowedActivity = String.join(", ",  Arrays.stream(ActivityType.values()).map(Enum::name).toList());
            throw new IllegalArgumentException("Invalid Specialization Type. Allowed values are: " + allowedActivity);
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Trainer trainer = new Trainer(requestDTO);
        trainer.setPassword(encoder.encode(requestDTO.getPassword()));
        trainer.setSpecialization(activityType.toString());
        return trainerRepository.save(trainer);
    }

    public Trainer getTrainerById(Long trainerID) {
        Optional<Trainer> trainer = trainerRepository.findById(trainerID);
        if(trainer.isEmpty()) {
            throw new EntityNotFoundException("No Trainer associated with this ID");
        }
        return trainer.get();
    }

    public List<UserResponse> getAllTrainer() {
        return userRepository.findByUserRole(UserRole.TRAINER)
                .stream()
                .map((user) -> new UserResponse(user))
                .toList();
    }
}
