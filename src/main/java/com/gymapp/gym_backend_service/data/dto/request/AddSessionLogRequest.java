package com.gymapp.gym_backend_service.data.dto.request;

public class AddSessionLogRequest {
    private String activityType;
    private Integer caloriesBurned;

    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }

    public int getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(int caloriesBurned) { this.caloriesBurned = caloriesBurned; }
}
