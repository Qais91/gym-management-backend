package com.gymapp.gym_backend_service.model.dto.response.registered_membership;

import com.gymapp.gym_backend_service.model.RegisteredMembership;
import com.gymapp.gym_backend_service.model.enums.RegistrationStatus;

public class RegMembershipOverviewInfoDTO {
    private Long id;
    private String memberName;
    private String planName;
    private int memberShipValidDuration;
    private RegistrationStatus status;
    private String validator;

    public RegMembershipOverviewInfoDTO(RegisteredMembership regMeberShip) {
        id = regMeberShip.getId();
        memberName = regMeberShip.getMember().getName();
        planName = regMeberShip.getMembership().getTitle();
        memberShipValidDuration = regMeberShip.getMembership().getDurationInMonths();
        status = regMeberShip.getStatus();
        validator = (regMeberShip.getValidator() != null) ? regMeberShip.getValidator().getName() : "-";
    }

    public Long getId() { return id; }
    public String getPlanName() { return planName; }
    public String getMemberName() { return memberName; }
    public String getValidator() { return validator; }
    public RegistrationStatus getStatus() { return status; }
    public int getMemberShipValidDuration() { return memberShipValidDuration; }
}
