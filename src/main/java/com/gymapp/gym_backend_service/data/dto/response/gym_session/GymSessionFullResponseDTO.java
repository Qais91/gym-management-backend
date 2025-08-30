package com.gymapp.gym_backend_service.data.dto.response.gym_session;

import com.gymapp.gym_backend_service.data.model.GymSession;

import java.time.LocalTime;

public class GymSessionFullResponseDTO extends SessionResponseDTO {
    public String notes;
    public LocalTime startTime;
    public  LocalTime endTime;

    public GymSessionFullResponseDTO(GymSession session) {
        super(session);

        notes = session.getNotes();
        startTime = LocalTime.from(session.getStartTime());
        endTime = LocalTime.from(session.getEndTime());
    }

    public String getNotes() { return (notes == null) ? "-" : notes; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
}
