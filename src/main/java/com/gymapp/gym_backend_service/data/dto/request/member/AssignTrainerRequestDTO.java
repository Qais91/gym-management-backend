package com.gymapp.gym_backend_service.data.dto.request.member;

import jakarta.validation.constraints.NotBlank;

public class AssignTrainerRequestDTO {
    private String memberName;
    @NotBlank(message = "Trainer Name is mandatory")
    private String trainerName;

    public String getMemberName() { return memberName; }
    public String getTrainerName() { return trainerName; }
}
