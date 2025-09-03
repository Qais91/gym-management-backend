package com.gymapp.gym_backend_service.repository;

import com.gymapp.gym_backend_service.data.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {
    @Query("SELECT CASE WHEN COUNT(ms) > 0 THEN true ELSE false END FROM Membership ms WHERE ms.title = :title and ms.durationInMonths = :monthDuration and ms.price = :price")
    boolean existsByMembershipPlan(@Param("title") String title, @Param("monthDuration") Integer monthDuration, @Param("price") double price);
}
