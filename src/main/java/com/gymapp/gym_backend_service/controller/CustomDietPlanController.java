package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.model.CustomDietPlan;
import com.gymapp.gym_backend_service.model.Diets;
import com.gymapp.gym_backend_service.repository.CustomDietPlanRepository;
import com.gymapp.gym_backend_service.repository.DietsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import com.gymapp.gym_backend_service.model.dto.request.DietAssignmentRequestDTO;

import java.util.List;
import java.util.Optional;

public class CustomDietPlanController {
    @Autowired
    private CustomDietPlanRepository customDietPlanRepository;
    @Autowired
    private DietsRepository dietsRepository;

    @PostMapping("/assign-diets")
    public ResponseEntity<?> assignDietsToPlan(@RequestBody DietAssignmentRequestDTO request) {
        Optional<CustomDietPlan> planOpt = customDietPlanRepository.findById(request.getDietPlanId());
        if (planOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Diet Plan not found");
        }

        CustomDietPlan plan = planOpt.get();
        List<Diets> dietsToAssign = dietsRepository.findAllById(request.getDietIds());

//        plan.getDiets().addAll(dietsToAssign);
         plan.setDiets(dietsToAssign);

        customDietPlanRepository.save(plan);
        return ResponseEntity.ok("Diets assigned successfully");
    }

}
