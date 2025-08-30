package com.gymapp.gym_backend_service.data.dto.response.gym_session;

import com.gymapp.gym_backend_service.data.model.GymSession;
import com.gymapp.gym_backend_service.data.enums.ActivityType;

import java.time.LocalDate;
import java.time.LocalTime;

public class CreateGymSessionResponseDTO {
    private String memberName;
    private String trainerName;
    private ActivityType activityType;
    private LocalDate sessionDate;
    private LocalTime sessionStartTime;
    private LocalTime sessionEndTime;
    private String message;

    public CreateGymSessionResponseDTO(GymSession session) {
        memberName = session.getMember().getUsername();
        trainerName = session.getTrainer().getUsername();
        activityType = session.getActivityType();
        sessionDate = session.getStartTime().toLocalDate();
        sessionStartTime = session.getStartTime().toLocalTime();
        sessionEndTime = session.getEndTime().toLocalTime();
        message = "Session Created";
    }

    public String getMemberName() { return this.memberName; }
    public String getTrainerName() { return this.trainerName; }
    public ActivityType getActivityType() { return this.activityType; }
    public LocalDate getSessionDate() { return this.sessionDate; }
    public LocalTime getSessionStartTime() { return this.sessionStartTime; }
    public LocalTime getSessionEndTime() { return this.sessionEndTime; }
    public String getMessage() { return message; }
}
