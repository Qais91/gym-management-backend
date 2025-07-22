package com.gymapp.gym_backend_service.repository;

import com.gymapp.gym_backend_service.model.CustomDietPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomDietPlanRepository extends JpaRepository<CustomDietPlan, Long> {
}
