package com.gymapp.gym_backend_service.repository;

import com.gymapp.gym_backend_service.data.model.InvoiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceDetailRepository extends JpaRepository<InvoiceDetail, Long> {
    List<InvoiceDetail> findByMemberId(Long customerId);
    InvoiceDetail findByRegisteredMembershipId(Long memberShipId);
}
