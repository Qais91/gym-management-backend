package com.gymapp.gym_backend_service.data.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CreateDietPlanRequestDTO {

    @NotNull(message="Title field is required")
    private String title;
    @NotNull(message="Creator ID is mandatory")
    private Long creatorId;
    private List<Integer> dietsList;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public void setDietsList(List<Integer> dietsList) {
        this.dietsList = dietsList;
    }

    public List<Integer> getDietsList() {
        return dietsList;
    }
}
