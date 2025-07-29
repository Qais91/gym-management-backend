package com.gymapp.gym_backend_service.model.dto.request;

public class CreateDietPlanRequestDTO {

    private String title;
    private Long memberId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
}
