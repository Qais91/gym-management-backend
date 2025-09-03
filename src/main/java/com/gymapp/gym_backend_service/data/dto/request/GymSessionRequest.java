package com.gymapp.gym_backend_service.data.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class GymSessionRequest {
    @NotNull(message = "Activity type is required")
    private String activityType;

    @NotNull(message = "Calories Burned is required")
    private Long caloriesBurned;

    private String notes;

    @NotNull(message = "Start time is required")
    @JsonFormat(pattern = "H:mm")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    @JsonFormat(pattern = "H:mm")
    private LocalTime endTime;


    public Long getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(Long caloriesBurned) { this.caloriesBurned=caloriesBurned; }

    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

}