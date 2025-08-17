package com.gymapp.gym_backend_service.model.dto.response.registered_membership;

import com.gymapp.gym_backend_service.model.CustomDietPlan;
import com.gymapp.gym_backend_service.model.RegisteredMemberships;
import com.gymapp.gym_backend_service.model.dto.response.custom_diet_plan.DietPlanResponseDTO;
import com.gymapp.gym_backend_service.model.enums.RegistrationStatus;

import java.time.LocalDate;
import java.util.List;

public class RegisteredMembershipInfoResponseDTO {

    private Long id;
    private String memberName;
    private String planName;
    private int memberShipValidDuration;
    private RegistrationStatus status;
    private String validator;
    private String uploadedDoc;
    private LocalDate endDate;
    private DietPlanResponseDTO customdietPlan;

    public RegisteredMembershipInfoResponseDTO(RegisteredMemberships regMeberShip) {
        id = regMeberShip.getId();
        memberName = regMeberShip.getMember().getName();
        planName = regMeberShip.getMembership().getTitle();
        memberShipValidDuration = regMeberShip.getMembership().getDurationInMonths();
        status = regMeberShip.getStatus();
        validator = (regMeberShip.getValidator() != null) ? regMeberShip.getValidator().getName() : "-";
        endDate = regMeberShip.getEndDate();
        customdietPlan = new DietPlanResponseDTO(regMeberShip.getDietPlan());
        uploadedDoc = "-";
    }

    public void setUploadedDoc(String docPath) { uploadedDoc = docPath; }

    public Long getId() { return id; }
    public String getPlanName() { return planName; }
    public String getMemberName() { return memberName; }
    public String getValidator() { return validator; }
    public int getMemberShipValidDuration() { return memberShipValidDuration; }
    public String getUploadedDoc() { return uploadedDoc; }
    public RegistrationStatus getStatus() { return status; }
    public DietPlanResponseDTO getCustomdietPlan() { return customdietPlan; }
    public LocalDate getEndDate() { return endDate; }
}
