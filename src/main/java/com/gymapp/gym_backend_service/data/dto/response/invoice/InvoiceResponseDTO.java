package com.gymapp.gym_backend_service.data.dto.response.invoice;

import com.gymapp.gym_backend_service.data.dto.response.registered_membership.RegMembershipOverviewInfoDTO;
import com.gymapp.gym_backend_service.data.model.InvoiceDetail;

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
