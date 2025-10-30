package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.data.dto.request.CreateDietPlanRequestDTO;
import com.gymapp.gym_backend_service.data.dto.request.diet.CreateDietRequestDTO;
import com.gymapp.gym_backend_service.data.model.Diets;
import com.gymapp.gym_backend_service.repository.DietsRepository;
import com.gymapp.gym_backend_service.service.DietsService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@ExtendWith(MockitoExtension.class)
public class DietsServiceTest {
    TestConfig testingConfig;

    @Mock
    DietsRepository dietsRepository;

    @InjectMocks
    DietsService dietsService;

    @BeforeEach
    void setUp() {
        testingConfig = new TestConfig();
    }

    @Test
    void testDietsCreation() {
        CreateDietRequestDTO createDietRequestDTO = new CreateDietRequestDTO();
        createDietRequestDTO.setMealType("Bunch");
        createDietRequestDTO.setCalories(testingConfig.test_diet1.getCalories());
        createDietRequestDTO.setPrice(testingConfig.test_diet1.getPrice());
        createDietRequestDTO.setFoodItem(testingConfig.test_diet1.getFoodItem());

        when(dietsRepository.existsByFoodItem(anyString())).thenReturn(false);
        when(dietsRepository.save(any(Diets.class))).thenReturn(testingConfig.test_diet1);

        Exception exception = assertThrows(EntityNotFoundException.class, () -> dietsService.createDiet(createDietRequestDTO));
        assertTrue("Diet creation Meal type test fail", exception.getMessage().contains("Invalid Meal Types"));

        createDietRequestDTO.setMealType(testingConfig.test_diet1.getMealType().toString());
        assertDoesNotThrow(() -> dietsService.createDiet(createDietRequestDTO));
    }
}
