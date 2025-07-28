package com.gymapp.gym_backend_service.model;

import jakarta.persistence.*;

@Entity
public class Diets {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mealType;

    @Column(length = 1000)
    private String foodItem;
    private Integer calories;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }

    public String getFoodItem() { return foodItem; }
    public void setFoodItem(String foodItem) { this.foodItem = foodItem; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }
}
