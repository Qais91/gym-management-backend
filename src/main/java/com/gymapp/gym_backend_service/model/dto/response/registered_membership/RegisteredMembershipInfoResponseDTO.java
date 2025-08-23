package com.gymapp.gym_backend_service.model.dto.response.registered_membership;

import com.gymapp.gym_backend_service.model.RegisteredMembership;
import com.gymapp.gym_backend_service.model.dto.response.custom_diet_plan.DietPlanResponseDTO;

import java.time.LocalDate;

public class RegisteredMembershipInfoResponseDTO extends RegMembershipOverviewInfoDTO {

    private String uploadedDoc;
    private LocalDate endDate;
    private DietPlanResponseDTO customdietPlan;

    public RegisteredMembershipInfoResponseDTO(RegisteredMembership regMemberShip) {
        super(regMemberShip);
        endDate = regMemberShip.getEndDate();
        customdietPlan = new DietPlanResponseDTO(regMemberShip.getDietPlan());
        uploadedDoc = "-";
    }

    public void setUploadedDoc(String docPath) { uploadedDoc = docPath; }

    public String getUploadedDoc() { return uploadedDoc; }
    public DietPlanResponseDTO getCustomdietPlan() { return customdietPlan; }
    public LocalDate getEndDate() { return endDate; }
}
