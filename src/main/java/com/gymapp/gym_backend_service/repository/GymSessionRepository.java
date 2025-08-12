package com.gymapp.gym_backend_service.repository;

import com.gymapp.gym_backend_service.model.GymSession;
import com.gymapp.gym_backend_service.model.Member;
import com.gymapp.gym_backend_service.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GymSessionRepository extends JpaRepository<GymSession, Long> {
    List<GymSession> findByMember(Member member);
    List<GymSession> findByTrainer(Trainer trainer);
}