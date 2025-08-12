package com.gymapp.gym_backend_service.model.dto.request;

import com.gymapp.gym_backend_service.model.Member;
import com.gymapp.gym_backend_service.model.Trainer;
import com.gymapp.gym_backend_service.model.enums.ActivityType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class GymSessionRequest {
    @NotNull(message = "Trainer ID is required")
    private Long trainerId;

    @NotNull(message = "Member ID is required")
    private Long memberId;
    private String notes;

    @NotNull(message = "Activity type is required")
    private String activityType;

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Long getMemberId() { return memberId; }
    public void setMember(Long memberId) { this.memberId = memberId; }

    public Long getTrainerId() { return trainerId; }
    public void setTrainer(Long trainerId) { this.trainerId = trainerId; }

    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

}