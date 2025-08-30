package com.gymapp.gym_backend_service.data.dto.request.memberShip;

import jakarta.validation.constraints.*;

public class CreateMemberShipDTO {
    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Price is required")
    private Long price;

    @NotNull(message = "Time period is required")
    private Integer timePeriodInMonth;

    private boolean needMedicalValidation = false;
    private boolean dietIncluded = false;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public double getPrice() { return price; }
    public void setPrice(Long priceVal) { this.price=priceVal; }

    public int getTimePeriodInMonth() { return timePeriodInMonth; }
    public void setTimePeriodInMonth(int timePeriodInMonth) { this.timePeriodInMonth = timePeriodInMonth; }

    public boolean getDietIncluded() { return dietIncluded; }
    public void setDietIncluded(boolean dietIncluded) { this.dietIncluded = dietIncluded; }

    public boolean getNeedMedicalValidation() { return needMedicalValidation; }
    public void setNeedMedicalValidation(boolean boolVal) { needMedicalValidation = boolVal; }
}
