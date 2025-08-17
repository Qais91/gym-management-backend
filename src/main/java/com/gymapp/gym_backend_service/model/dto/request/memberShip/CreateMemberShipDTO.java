package com.gymapp.gym_backend_service.model.dto.request.memberShip;

import jakarta.validation.constraints.*;

public class CreateMemberShipDTO {
    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Price is required")
    private Long price;

    @NotNull(message = "Time period is required")
    private Integer timePeriod;

    private boolean needMedicalValidation = false;
    private boolean dietIncluded = false;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public double getPrice() { return price; }
    public void setPrice(Long priceVal) { this.price=priceVal; }

    public int getTimePeriod() { return timePeriod; }
    public void setTimePeriod(int timePeriod) { this.timePeriod = timePeriod; }

    public boolean getDietIncluded() { return dietIncluded; }
    public void setDietIncluded(boolean dietIncluded) { this.dietIncluded = dietIncluded; }

    public boolean getNeedMedicalValidation() { return needMedicalValidation; }
    public void setNeedMedicalValidation(boolean boolVal) { needMedicalValidation = boolVal; }
}
