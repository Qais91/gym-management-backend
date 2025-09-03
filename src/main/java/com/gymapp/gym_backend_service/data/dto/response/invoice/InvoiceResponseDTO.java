package com.gymapp.gym_backend_service.data.dto.response.invoice;

import com.gymapp.gym_backend_service.data.dto.response.registered_membership.RegMembershipOverviewInfoDTO;
import com.gymapp.gym_backend_service.data.model.InvoiceDetail;

public class InvoiceResponseDTO extends RegMembershipOverviewInfoDTO {
    private Long invoiceId;
    private double totalFee;
    private String invoiceStatus;

    public InvoiceResponseDTO(InvoiceDetail invoiceDetail) {
        super(invoiceDetail.getRegisteredMembership());
        invoiceId = invoiceDetail.getId();
        totalFee = invoiceDetail.getAmount();
        invoiceStatus = invoiceDetail.getStatus().name();
    }

    public Long getInvoiceId() { return invoiceId; }
    public double getTotalFee() { return totalFee; }
    public String getInvoiceStatus() { return invoiceStatus; }
}
