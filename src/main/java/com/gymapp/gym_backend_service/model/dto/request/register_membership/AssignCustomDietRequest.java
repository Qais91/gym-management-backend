package com.gymapp.gym_backend_service.model.dto.request.register_membership;

import jakarta.validation.constraints.NotNull;

public class AssignCustomDietRequest {
    @NotNull(message = "Registered membership ID is mandatory field")
    private Long regMemberShipID;
    @NotNull(message = "Custom Diet ID is required field")
    private Long customDietID;

    public Long getRegMemberShipID() { return regMemberShipID; }
    public Long getCustomDietID() { return customDietID; }
}
