package com.gymapp.gym_backend_service.data.dto.response.custom_diet_plan;

import com.gymapp.gym_backend_service.data.dto.response.DietSummaryDTO;
import com.gymapp.gym_backend_service.data.model.CustomDietPlan;

import java.util.List;

public class DietPlanResponseDTO {
    private double id;
    private String title;
    private String createdBy;
    private List<DietSummaryDTO> diets;

    public DietPlanResponseDTO(CustomDietPlan customDietPlan) {
        id = customDietPlan.getId();
        title = customDietPlan.getTitle();
        createdBy = customDietPlan.getCreatedBy().getUsername();
        diets = !customDietPlan.getDiets().isEmpty() ? customDietPlan.getDiets().stream().map((diet) -> new DietSummaryDTO(diet)).toList() : null;
    }

    public double getId() { return id; }
    public String getTitle() { return title; }
    public String getCreatedBy() { return createdBy; }
    public List<DietSummaryDTO> getDiets() { return diets; }
}
