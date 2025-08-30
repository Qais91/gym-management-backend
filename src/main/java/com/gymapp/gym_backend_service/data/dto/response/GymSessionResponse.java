package com.gymapp.gym_backend_service.data.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class GymSessionResponse {
    private Long sessionId;
    private String memberName;
    private String trainerName;
    private String notes;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<SessionLogResponse> activities;

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }

    public String getTrainerName() { return trainerName; }
    public void setTrainerName(String trainerName) { this.trainerName = trainerName; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public List<SessionLogResponse> getActivities() { return activities; }
    public void setActivities(List<SessionLogResponse> sessionLogResponses) { this.activities = sessionLogResponses; }
}
