package com.gymapp.gym_backend_service.model.dto.request;

import java.time.LocalDateTime;

public class CreateGymSessionRequest {
    private Long memberId;
    private Long trainerId;
    private String notes;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberID) { this.memberId = memberID; }

    public Long getTrainerId() { return trainerId; }
    public void setTrainerId(Long trainerID) { this.trainerId = trainerID; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startDateTime) { this.startTime = startDateTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endDateTime) { this.endTime = endDateTime; }
}