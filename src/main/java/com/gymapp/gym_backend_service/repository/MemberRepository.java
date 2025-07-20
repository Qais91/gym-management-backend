package com.gymapp.gym_backend_service.repository;

import com.gymapp.gym_backend_service.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
}
