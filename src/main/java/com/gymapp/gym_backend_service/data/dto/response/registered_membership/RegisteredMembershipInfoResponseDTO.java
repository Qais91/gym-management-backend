package com.gymapp.gym_backend_service.data.dto.response.registered_membership;

import com.gymapp.gym_backend_service.data.dto.response.custom_diet_plan.DietPlanResponseDTO;
import com.gymapp.gym_backend_service.data.model.RegisteredMembership;

public class RegisteredMembershipInfoResponseDTO extends RegMembershipOverviewInfoDTO {

    private String uploadedDoc;
    private String endDate;
    private DietPlanResponseDTO customDietPlan;

    public RegisteredMembershipInfoResponseDTO(RegisteredMembership regMemberShip) {
        super(regMemberShip);
        endDate = (regMemberShip.getEndDate() == null) ? "-" : regMemberShip.getEndDate().toString();
        customDietPlan = (regMemberShip.getDietPlan() != null) ? new DietPlanResponseDTO(regMemberShip.getDietPlan()) : null;
        uploadedDoc = "-";
    }

    public void setUploadedDoc(String docPath) { uploadedDoc = docPath; }

    public String getUploadedDoc() { return uploadedDoc; }
    public DietPlanResponseDTO getCustomDietPlan() { return customDietPlan; }
    public String getEndDate() { return endDate; }
}
