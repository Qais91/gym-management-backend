package com.gymapp.gym_backend_service.repository;

import com.gymapp.gym_backend_service.data.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    Trainer findByUsername(String trainerName);
}
