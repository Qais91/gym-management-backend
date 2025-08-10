package com.gymapp.gym_backend_service.model.dto.response;

public class SessionLogResponse {
    private String activityType;
    private Integer caloriesBurned;

    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }

    public int getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(int caloriesBurned) { this.caloriesBurned = caloriesBurned; }
}
