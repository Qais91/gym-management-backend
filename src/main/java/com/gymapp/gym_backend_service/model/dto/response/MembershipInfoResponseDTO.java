package com.gymapp.gym_backend_service.model.dto.response;

import java.time.LocalDate;

public class MembershipInfoResponseDTO {

    private String planName;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;

    public MembershipInfoResponseDTO(String planName, LocalDate startDate, LocalDate endDate, boolean active) {
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
