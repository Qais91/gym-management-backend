package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.model.CustomDietPlan;
import com.gymapp.gym_backend_service.model.Diets;
import com.gymapp.gym_backend_service.model.Member;
import com.gymapp.gym_backend_service.model.dto.request.CreateDietPlanRequestDTO;
import com.gymapp.gym_backend_service.model.dto.request.UpdateDietPlanRequestDTO;
import com.gymapp.gym_backend_service.model.dto.response.CustomDietPlanResponseDTO;
import com.gymapp.gym_backend_service.model.dto.response.DietSummaryDTO;
import com.gymapp.gym_backend_service.repository.CustomDietPlanRepository;
import com.gymapp.gym_backend_service.repository.DietsRepository;
import com.gymapp.gym_backend_service.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import com.gymapp.gym_backend_service.model.dto.request.DietAssignmentRequestDTO;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/diet-plans")
public class CustomDietPlanController {
    @Autowired
    private CustomDietPlanRepository customDietPlanRepository;
    @Autowired
    private DietsRepository dietsRepository;
    @Autowired
    private MemberRepository memberRepository;

    @GetMapping("/member/{memberId}")
    public ResponseEntity<?> getPlansByMember(@PathVariable Long memberId) {
        List<CustomDietPlan> plans = customDietPlanRepository.findByMemberId(memberId);
        return ResponseEntity.ok(plans);
    }

    @PostMapping
    public ResponseEntity<?> createCustomDietPlan(@RequestBody CreateDietPlanRequestDTO request) {
        Optional<Member> memberOpt = memberRepository.findById(request.getMemberId());
        if (memberOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid member ID");
        }

        CustomDietPlan plan = new CustomDietPlan();
        plan.setTitle(request.getTitle());
        plan.setMember(memberOpt.get());

        CustomDietPlan savedPlan = customDietPlanRepository.save(plan);
        return ResponseEntity.ok(savedPlan);
    }


    @PostMapping("/assign-diets")
    public ResponseEntity<?> assignDietsToPlan(@RequestBody DietAssignmentRequestDTO request) {
        Optional<CustomDietPlan> planOpt = customDietPlanRepository.findById(request.getDietPlanId());
        if (planOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Diet Plan not found");
        }

        CustomDietPlan plan = planOpt.get();
        List<Diets> dietsToAssign = dietsRepository.findAllById(request.getDietIds());

         plan.setDiets(dietsToAssign);

        customDietPlanRepository.save(plan);
        return ResponseEntity.ok("Diets assigned successfully");
    }

    @PutMapping("/update-diet")
    public ResponseEntity<?> updateDietPlan(@RequestBody UpdateDietPlanRequestDTO request) {
        Optional<CustomDietPlan> planOpt = customDietPlanRepository.findById(request.getDietPlanId());
        if (planOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Diet Plan not found");
        }

        CustomDietPlan plan = planOpt.get();

        if (request.getNewTitle() != null && !request.getNewTitle().isBlank()) {
            plan.setTitle(request.getNewTitle());
        }

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
        responseDTO.setMemberName(updatedPlan.getMember().getName());

        List<DietSummaryDTO> dietSummaries = updatedPlan.getDiets().stream().map(d -> {
            DietSummaryDTO dto = new DietSummaryDTO();
            dto.setId(d.getId());
            dto.setMealType(d.getMealType());
            dto.setFoodItem(d.getFoodItem());
            dto.setCalories(d.getCalories());
            return dto;
        }).toList();

        responseDTO.setDiets(dietSummaries);

        return ResponseEntity.ok(responseDTO);

    }

}
