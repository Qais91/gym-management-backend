package com.gymapp.gym_backend_service.repository;

import com.gymapp.gym_backend_service.data.model.CustomDietPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomDietPlanRepository extends JpaRepository<CustomDietPlan, Long> {
    List<CustomDietPlan> findByCreatedById(Long trainerId);
}
