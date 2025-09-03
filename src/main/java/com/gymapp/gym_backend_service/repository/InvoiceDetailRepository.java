package com.gymapp.gym_backend_service.repository;

import com.gymapp.gym_backend_service.data.model.InvoiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InvoiceDetailRepository extends JpaRepository<InvoiceDetail, Long> {
    List<InvoiceDetail> findByMemberId(Long customerId);
    InvoiceDetail findByRegisteredMembershipId(Long memberShipId);

    @Query("Select id from InvoiceDetail id where id.registeredMembership.id = :memberShipId and id.status = 'DENIED'")
    Optional<InvoiceDetail> findDeniedInvoiceByMemberShip(@Param("memberShipId") Long memberShipId);

    @Query("SELECT i FROM InvoiceDetail i WHERE i.member.id = :memberId AND i.status = 'PENDING'")
    List<InvoiceDetail> findPendingInvoicesByMember(@Param("memberId") Long memberId);
}
