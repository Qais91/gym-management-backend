package com.gymapp.gym_backend_service.model.dto.response.gym_session;

import com.gymapp.gym_backend_service.model.GymSession;
import com.gymapp.gym_backend_service.model.enums.ActivityType;

import java.time.LocalDate;

public class SessionResponseDTO {
    private Long sessionId;
    private String memberName;
    private ActivityType activity;
    private String trainerName;
    private LocalDate sessionDate;

    public SessionResponseDTO(GymSession session) {
        sessionId = session.getId();
        memberName = session.getMember().getUsername();
        trainerName = session.getTrainer().getUsername();
        sessionDate = session.getStartTime().toLocalDate();
        activity = session.getActivityType();
    }

    public Long getSessionId() { return this.sessionId; }
    public String getMemberName() { return this.memberName; }
    public String getTrainerName() { return this.trainerName; }
    public ActivityType getActivityType() { return this.activity; }
    public LocalDate getSessionDate() { return this.sessionDate; }

}
