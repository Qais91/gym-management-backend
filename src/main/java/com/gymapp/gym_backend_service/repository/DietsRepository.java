package com.gymapp.gym_backend_service.repository;

import com.gymapp.gym_backend_service.data.enums.DietMealType;
import com.gymapp.gym_backend_service.data.model.Diets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DietsRepository extends JpaRepository<Diets, Long> {
    boolean existsByFoodItem(String foodItem);
    boolean existsByCalories(Integer calories);
    boolean existsByMealType(DietMealType mealType);
    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM Diets d WHERE d.foodItem = :foodItem and d.calories = :calories and d.mealType = :mealType")
    boolean existsByDiet(@Param("foodItem") String foodItem, @Param("calories") Integer calories, @Param("mealType") DietMealType mealType);
}
