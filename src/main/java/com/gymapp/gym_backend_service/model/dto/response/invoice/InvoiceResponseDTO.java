package com.gymapp.gym_backend_service.model.dto.response.invoice;

import com.gymapp.gym_backend_service.model.InvoiceDetail;
import com.gymapp.gym_backend_service.model.dto.response.registered_membership.RegMembershipOverviewInfoDTO;

public class InvoiceResponseDTO {
    private Long invoiceId;
    private double totalFee;
    private RegMembershipOverviewInfoDTO regMemberShip;

    public InvoiceResponseDTO(InvoiceDetail invoiceDetail) {
        invoiceId = invoiceDetail.getId();
        totalFee = invoiceDetail.getAmount();
        regMemberShip = invoiceDetail.getRegisteredMembership() != null ? new RegMembershipOverviewInfoDTO(invoiceDetail.getRegisteredMembership()) : null;
    }

    public Long getInvoiceId() { return invoiceId; }
    public double getTotalFee() { return totalFee; }
    public RegMembershipOverviewInfoDTO getRegMemberShip() { return regMemberShip; }
}
