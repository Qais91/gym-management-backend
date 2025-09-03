package com.gymapp.gym_backend_service.data.dto.response.gym_session;

import com.gymapp.gym_backend_service.data.model.GymSession;
import com.gymapp.gym_backend_service.data.enums.ActivityType;

import java.time.LocalDate;
import java.time.LocalTime;

public class CreateGymSessionResponseDTO extends SessionResponseDTO {
    private LocalTime sessionStartTime;
    private LocalTime sessionEndTime;

    public CreateGymSessionResponseDTO(GymSession session) {
        super(session);
        sessionStartTime = session.getStartTime().toLocalTime();
        sessionEndTime = session.getEndTime().toLocalTime();
    }

    public LocalTime getSessionStartTime() { return this.sessionStartTime; }
    public LocalTime getSessionEndTime() { return this.sessionEndTime; }
}
