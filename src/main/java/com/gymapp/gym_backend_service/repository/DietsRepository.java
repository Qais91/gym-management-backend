package com.gymapp.gym_backend_service.repository;

import com.gymapp.gym_backend_service.model.Diets;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DietsRepository extends JpaRepository<Diets, Long> {
}
