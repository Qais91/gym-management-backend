package com.gymapp.gym_backend_service.repository;

import com.gymapp.gym_backend_service.data.model.InvoiceDetail;
import com.gymapp.gym_backend_service.data.model.RegisteredMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RegisteredMembershipsRepository extends JpaRepository<RegisteredMembership, Long> {
    List<RegisteredMembership> findByMemberId(Long memberId);
    List<RegisteredMembership> findByValidatorId(Long trainerId);
    Optional<RegisteredMembership> findByIdAndEndDateBefore(Long id, LocalDate currentDate);

    @Query("Select rm from RegisteredMembership rm where rm.member.id= :memberId and rm.status = 'REGISTERED' and rm.endDate > CURRENT_DATE")
    Optional<RegisteredMembership> findActiveRegisteredMemberShip(@Param("memberId") Long memberId);

    @Query("Select rm from RegisteredMembership rm where rm.member.id= :memberId and rm.status in ('PENDING', 'APPLIED', 'REVIEWED')")
    List<RegisteredMembership> findPendingRegisteredMemberShip(@Param("memberId") Long memberId);

//    @Query("Select rm from RegisteredMembership rm where rm.member.id= :memberId and rm.status = 'REGISTERED' and rm.endDate > CURRENT_DATE")
    @Query("SELECT CASE WHEN COUNT(rm) > 0 THEN true ELSE false END FROM RegisteredMembership rm WHERE rm.member.id= :memberId and rm.status = 'REGISTERED' and rm.endDate > CURRENT_DATE")
    boolean isMemberShipActive(@Param("memberId") Long memberId);
}