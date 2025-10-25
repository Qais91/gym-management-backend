package com.gymapp.gym_backend_service.service;

import com.gymapp.gym_backend_service.authorization.JWTHandler;
import com.gymapp.gym_backend_service.data.dto.request.CreateDietPlanRequestDTO;
import com.gymapp.gym_backend_service.data.dto.request.DietAssignmentRequestDTO;
import com.gymapp.gym_backend_service.data.dto.request.custom_diet.UpdateDietPlanRequestDTO;
import com.gymapp.gym_backend_service.data.dto.response.ApiResponse;
import com.gymapp.gym_backend_service.data.dto.response.CustomDietPlanResponseDTO;
import com.gymapp.gym_backend_service.data.dto.response.DietSummaryDTO;
import com.gymapp.gym_backend_service.data.dto.response.custom_diet_plan.DietPlanResponseDTO;
import com.gymapp.gym_backend_service.data.model.CustomDietPlan;
import com.gymapp.gym_backend_service.data.model.Diets;
import com.gymapp.gym_backend_service.data.model.Trainer;
import com.gymapp.gym_backend_service.repository.CustomDietPlanRepository;
import com.gymapp.gym_backend_service.repository.DietsRepository;
import com.gymapp.gym_backend_service.repository.TrainerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CustomDietPlanService {
    @Autowired
    private CustomDietPlanRepository customDietPlanRepository;
    @Autowired
    private DietsRepository dietsRepository;
    @Autowired
    private TrainerRepository trainerRepository;
    @Autowired
    private CommonService commonService;

    public List<DietPlanResponseDTO> getAssignedDietPlan(String header) {
        Long validatorId = commonService.getMemberID(header);
        List<CustomDietPlan> plans = customDietPlanRepository.findByCreatedById(validatorId);
        Optional<Trainer> trainer = trainerRepository.findById(validatorId);

        if(trainer.isEmpty()) throw new EntityNotFoundException("Not valid trainer id");
        if(plans.isEmpty()) throw new EntityNotFoundException("No Diet Plans so far");

        List<DietPlanResponseDTO> result = plans.stream().map((dietPlan) -> new DietPlanResponseDTO(dietPlan)).toList();
        return result;
    }

    public List<DietPlanResponseDTO> getAllDietPlan() {
        List<CustomDietPlan> customDietPlans = customDietPlanRepository.findAll();

        List<DietPlanResponseDTO> result = customDietPlans.stream().map((dietPlan) -> new DietPlanResponseDTO(dietPlan)).toList();
        return result;
    }

    public CustomDietPlan createCustomDietPlan(String header, CreateDietPlanRequestDTO request) {
        Long trainerId = commonService.getMemberID(header);
        Optional<Trainer> trainerOpt = trainerRepository.findById(trainerId);
        List<Diets> assignedDiets = (request.getDietsList() != null) ? request.getDietsList().stream().map(idVal -> {
            Optional<Diets> assignedDiet = dietsRepository.findById(Long.valueOf(idVal));
            return assignedDiet.orElse(null);
        }).toList() : null;

        if (trainerOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid Trainer ID");
        }

        CustomDietPlan plan = new CustomDietPlan();
        plan.setTitle(request.getTitle());
        plan.setCreatedBy(trainerOpt.get());
        if(assignedDiets != null) {
            if (assignedDiets.stream().anyMatch(Objects::isNull)) throw new IllegalArgumentException("One of diet is invalid. Enter valid diets list");
            plan.setDiets(assignedDiets);
        }

        return customDietPlanRepository.save(plan);
    }

    public void assignDietToPlan(DietAssignmentRequestDTO request) {
        Optional<CustomDietPlan> planOpt = customDietPlanRepository.findById(request.getDietPlanId());
        if (planOpt.isEmpty()) { throw new EntityNotFoundException("Diet Plan not found"); }

        CustomDietPlan plan = planOpt.get();
        List<Diets> dietsToAssign = dietsRepository.findAllById(request.getDietsId());

        if (dietsToAssign.isEmpty()) { throw new EntityNotFoundException("Diets not found enter valid diet ID"); }

        plan.setDiets(dietsToAssign);
        customDietPlanRepository.save(plan);
    }

    public CustomDietPlanResponseDTO updateDietPlan(UpdateDietPlanRequestDTO request) {
        Optional<CustomDietPlan> planOpt = customDietPlanRepository.findById(request.getDietPlanId());
        if (planOpt.isEmpty()) { throw new EntityNotFoundException("Diet Plan not found"); }

        CustomDietPlan plan = planOpt.get();

        if (request.getNewTitle() != null && !request.getNewTitle().isBlank()) { plan.setTitle(request.getNewTitle()); }

        if (request.getDietIdsToAdd() != null && !request.getDietIdsToAdd().isEmpty()) {
            List<Diets> dietsToAdd = dietsRepository.findAllById(request.getDietIdsToAdd());
            plan.getDiets().addAll(dietsToAdd);
        }

        if (request.getDietIdsToRemove() != null && !request.getDietIdsToRemove().isEmpty()) {
            plan.getDiets().removeIf(d -> request.getDietIdsToRemove().contains(d.getId()));
        }

        CustomDietPlan updatedPlan = customDietPlanRepository.save(plan);

        CustomDietPlanResponseDTO responseDTO = new CustomDietPlanResponseDTO();
        responseDTO.setId(updatedPlan.getId());
        responseDTO.setTitle(updatedPlan.getTitle());
        responseDTO.setCreatedBy(updatedPlan.getCreatedBy().getName());

        List<DietSummaryDTO> dietSummaries = updatedPlan.getDiets().stream().map(d -> {
            DietSummaryDTO dto = new DietSummaryDTO();
            dto.setId(d.getId());
            dto.setMealType((d.getMealType() != null) ? d.getMealType().name() : "-");
            dto.setFoodItem(d.getFoodItem());
            dto.setCalories(d.getCalories());
            return dto;
        }).toList();

        responseDTO.setDiets(dietSummaries);
        return responseDTO;
    }
}
