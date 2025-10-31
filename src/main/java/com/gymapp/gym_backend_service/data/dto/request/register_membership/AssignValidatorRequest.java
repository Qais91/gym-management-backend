package com.gymapp.gym_backend_service.data.dto.request.register_membership;

import jakarta.validation.constraints.NotNull;

public class AssignValidatorRequest {
    @NotNull(message = "Registered Membership Id is required")
    private Long regMemberShipID;
    @NotNull(message = "Trainer ID is required")
    private Long trainerID;

    public AssignValidatorRequest(Long regMemberShipID, Long trainerID) {
        this.regMemberShipID = regMemberShipID;
        this.trainerID = trainerID;
    }

    public Long getRegMemberShipID() { return regMemberShipID; }
    public Long getTrainerID() { return trainerID; }
}
