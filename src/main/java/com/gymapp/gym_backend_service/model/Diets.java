package com.gymapp.gym_backend_service.model;

import jakarta.persistence.*;

@Entity
public class Diets {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mealType;

    @Column(length = 1000)
    private String foodItems;

    private Integer calories;

    @ManyToOne
    @JoinColumn(name = "custom_diet_plan_id")
    private CustomDietPlan customDietPlan;
}
