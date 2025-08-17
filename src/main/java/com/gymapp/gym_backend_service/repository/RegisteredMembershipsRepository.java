package com.gymapp.gym_backend_service.repository;

import com.gymapp.gym_backend_service.model.RegisteredMemberships;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RegisteredMembershipsRepository extends JpaRepository<RegisteredMemberships, Long> {
    List<RegisteredMemberships> findByMemberId(Long memberId);
    List<RegisteredMemberships> findByValidatorId(Long trainerId);
}