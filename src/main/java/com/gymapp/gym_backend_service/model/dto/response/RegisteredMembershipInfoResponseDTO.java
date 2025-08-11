package com.gymapp.gym_backend_service.model.dto.response;

import java.time.LocalDate;

public class RegisteredMembershipInfoResponseDTO {

    private String planName;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;

    public RegisteredMembershipInfoResponseDTO(String planName, LocalDate startDate, LocalDate endDate, boolean active) {
        this.planName = planName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
    }

    public String getPlanName() { return planName; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public boolean isActive() { return active; }
}
