package com.gymapp.gym_backend_service.service;

import com.gymapp.gym_backend_service.data.dto.request.diet.CreateDietRequestDTO;
import com.gymapp.gym_backend_service.data.dto.response.ApiResponse;
import com.gymapp.gym_backend_service.data.enums.DietMealType;
import com.gymapp.gym_backend_service.data.model.Diets;
import com.gymapp.gym_backend_service.repository.DietsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class DietsService {

    @Autowired
    private DietsRepository dietsRepository;

    public Diets createDiet(CreateDietRequestDTO requestDiet) {
        Diets diet = new Diets(requestDiet);

        if(requestDiet.getMealType() != null) {
            DietMealType mealType;
            try {
                mealType = DietMealType.valueOf(requestDiet.getMealType().toUpperCase());
            } catch (Exception e) {
                String mealTypes = String.join(", ",  Arrays.stream(DietMealType.values()).map(Enum::name).toList());
                throw new EntityNotFoundException("Invalid Meal Types. Allowed values are: " + mealTypes);
            }
            diet.setMealType(mealType);
        }

        if(dietsRepository.existsByFoodItem(diet.getFoodItem()) &&
                dietsRepository.existsByCalories(diet.getCalories()) &&
                dietsRepository.existsByMealType(diet.getMealType())) {
            throw new IllegalArgumentException("Food with same calories available is in Menu");
        }

        return dietsRepository.save(diet);
    }

    public Diets getDietByID(Long id) {
        Optional<Diets> diet = dietsRepository.findById(id);
        if(diet.isEmpty()) { throw new EntityNotFoundException("No Item Found"); }
        return diet.get();
    }

    public List<Diets> getAllDiets() {
        List<Diets> diets = dietsRepository.findAll();
        if(diets.isEmpty()) { throw new EntityNotFoundException("No Diets"); }
        return diets;
    }

}
