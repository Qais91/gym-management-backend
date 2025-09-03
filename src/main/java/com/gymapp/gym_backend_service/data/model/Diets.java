package com.gymapp.gym_backend_service.data.model;

import com.gymapp.gym_backend_service.data.dto.request.diet.CreateDietRequestDTO;
import com.gymapp.gym_backend_service.data.enums.DietMealType;
import jakarta.persistence.*;

@Entity
public class Diets {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DietMealType mealType;

    @Column(length = 1000)
    private String foodItem;
    private Integer calories;

    private Integer price = 100;

    Diets() {}

    public Diets(String foodItem, DietMealType mealType, Integer calories, Integer price) {
        this.foodItem = foodItem;
        this.calories = calories;
        this.mealType = mealType;
        this.price = price;
    }

    public Diets(CreateDietRequestDTO diets) {
        foodItem = diets.getFoodItem();
        calories = diets.getCalories();
        price = (diets.getPrice() == null) ? price : diets.getPrice();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public DietMealType getMealType() { return mealType; }
    public void setMealType(DietMealType mealType) { this.mealType = mealType; }

    public String getFoodItem() { return foodItem; }
    public void setFoodItem(String foodItem) { this.foodItem = foodItem; }

    public Integer getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }

    public Integer getPrice() { return price; }
    public void setPrice(Integer priceVal) { price = priceVal; }
}
