package com.gymapp.gym_backend_service.model.dto.request;

import java.util.List;

public class DietAssignmentRequestDTO {

    private Long dietPlanId;
    private List<Long> dietIds;

    // Getters and Setters
    public Long getDietPlanId() {
        return dietPlanId;
    }

    public void setDietPlanId(Long dietPlanId) {
        this.dietPlanId = dietPlanId;
    }

    public List<Long> getDietIds() {
        return dietIds;
    }

    public void setDietIds(List<Long> dietIds) {
        this.dietIds = dietIds;
    }
}
