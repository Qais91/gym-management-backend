package com.gymapp.gym_backend_service.repository;

import com.gymapp.gym_backend_service.data.model.RegisteredMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RegisteredMembershipsRepository extends JpaRepository<RegisteredMembership, Long> {
    List<RegisteredMembership> findByMemberId(Long memberId);
    List<RegisteredMembership> findByValidatorId(Long trainerId);
}