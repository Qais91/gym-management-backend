package com.gymapp.gym_backend_service.data.dto.response;

import com.gymapp.gym_backend_service.data.model.Diets;

public class DietSummaryDTO {

    private Long id;
    private String mealType;
    private String foodItem;
    private int calories;

    public DietSummaryDTO() {}

    public DietSummaryDTO(Diets diet) {
        id = diet.getId();
        mealType = diet.getMealType();
        foodItem = diet.getFoodItem();
        calories = diet.getCalories();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public String getFoodItem() {
        return foodItem;
    }

    public void setFoodItem(String foodItem) {
        this.foodItem = foodItem;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }
}