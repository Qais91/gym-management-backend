package com.gymapp.gym_backend_service.data.dto.request.diet;

import com.gymapp.gym_backend_service.data.enums.DietMealType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateDietRequestDTO {
    @NotBlank(message = "Food name should not be blank")
    @NotNull(message = "Food Item is mandatory")
    private String foodItem;
    @NotNull(message = "Calories is mandatory field")
    private Integer calories;
    @NotNull(message = "Price is mandatory field")
    private Integer price;
    public String mealType;

    public String getMealType() { return mealType; }

    public Integer getCalories() { return calories; }

    public Integer getPrice() { return price; }

    public String getFoodItem() { return foodItem; }
}
