package com.gymapp.gym_backend_service.repository;

import com.gymapp.gym_backend_service.model.GymSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GymSessionRepository extends JpaRepository<GymSession, Long> {
}
