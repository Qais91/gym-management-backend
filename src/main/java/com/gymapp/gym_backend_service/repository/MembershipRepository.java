package com.gymapp.gym_backend_service.repository;

import com.gymapp.gym_backend_service.data.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {
}
