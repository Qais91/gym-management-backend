package com.gymapp.gym_backend_service.model.dto.request;

import java.util.List;

public class UpdateDietPlanRequestDTO {
    private Long dietPlanId;
    private String newTitle;

    private List<Long> dietIdsToAdd;
    private List<Long> dietIdsToRemove;

    public Long getDietPlanId() {
        return dietPlanId;
    }

    public void setDietPlanId(Long dietPlanId) {
        this.dietPlanId = dietPlanId;
    }

    public String getNewTitle() {
        return newTitle;
    }

    public void setNewTitle(String newTitle) {
        this.newTitle = newTitle;
    }

    public List<Long> getDietIdsToAdd() {
        return dietIdsToAdd;
    }

    public void setDietIdsToAdd(List<Long> dietIdsToAdd) {
        this.dietIdsToAdd = dietIdsToAdd;
    }

    public List<Long> getDietIdsToRemove() {
        return dietIdsToRemove;
    }

    public void setDietIdsToRemove(List<Long> dietIdsToRemove) {
        this.dietIdsToRemove = dietIdsToRemove;
    }

}
