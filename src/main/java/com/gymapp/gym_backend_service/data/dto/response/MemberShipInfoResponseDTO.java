package com.gymapp.gym_backend_service.data.dto.response;

import com.gymapp.gym_backend_service.data.model.Membership;

public class MemberShipInfoResponseDTO {
    private Long membershipID;
    private String planName;
    private double price;
    private boolean documentRequired;

    public MemberShipInfoResponseDTO(Membership membership) {
        membershipID = membership.getId();
        planName = membership.getTitle();
        price = membership.getPrice();
        documentRequired = membership.getMedicalValidationRequired();
    }

    public Long getMembershipID() { return membershipID; }
    public String getPlanName() { return planName; }
    public double getPrice() { return price; }
    public boolean isDocumentRequired() { return documentRequired; }
}
