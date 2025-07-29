package com.gymapp.gym_backend_service.model.dto.response;

import java.util.List;

public class CustomDietPlanResponseDTO {

    private Long id;
    private String title;
    private String memberName;
    private List<DietSummaryDTO> diets;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public List<DietSummaryDTO> getDiets() {
        return diets;
    }

    public void setDiets(List<DietSummaryDTO> diets) {
        this.diets = diets;
    }
}
