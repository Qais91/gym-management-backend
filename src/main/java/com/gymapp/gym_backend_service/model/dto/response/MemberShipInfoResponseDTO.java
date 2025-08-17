package com.gymapp.gym_backend_service.model.dto.response;

import com.gymapp.gym_backend_service.model.Membership;

public class MemberShipInfoResponseDTO {
    private Long membershipID;
    private String planName;
    private double price;
    private boolean documentRequired;

    public MemberShipInfoResponseDTO(Membership membership) {
        membershipID = membership.getId();
        planName = membership.getTitle();
        price = membership.getPricePerDurationMonth();
        documentRequired = membership.getMedicalValidationRequired();
    }

    public Long getMembershipID() { return membershipID; }
    public String getPlanName() { return planName; }
    public double getPrice() { return price; }
    public boolean isDocumentRequired() { return documentRequired; }
}
