package com.gymapp.gym_backend_service.repository;

import com.gymapp.gym_backend_service.model.RegisteredMemberships;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegisteredMembershipsRepository extends JpaRepository<RegisteredMemberships, Long> {
}