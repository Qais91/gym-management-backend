package com.gymapp.gym_backend_service.data.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class DietAssignmentRequestDTO {

    @NotNull(message = "Kindly enter diet plan ID")
    private Long dietPlanId;

    @NotNull(message = "Diets ID is required field")
    @NotEmpty(message = "Deits id should not be an empty field")
    private List<Long> dietsId;

    public Long getDietPlanId() {
        return dietPlanId;
    }

    public void setDietPlanId(Long dietPlanId) {
        this.dietPlanId = dietPlanId;
    }

    public List<Long> getDietsId() {
        return dietsId;
    }

    public void setDietsId(List<Long> dietsId) {
        this.dietsId = dietsId;
    }
}
