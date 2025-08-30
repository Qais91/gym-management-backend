package com.gymapp.gym_backend_service.data.model;

import com.gymapp.gym_backend_service.data.dto.request.memberShip.CreateMemberShipDTO;
import jakarta.persistence.*;

@Entity
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Integer durationInMonths;
    private double price;
    private boolean dietsIncluded = false;
    private boolean medicalValidationRequired = false;

    public Membership() {}

    public Membership(CreateMemberShipDTO memberShipDTO) {
        title = memberShipDTO.getTitle();
        durationInMonths = memberShipDTO.getTimePeriodInMonth();
        price = memberShipDTO.getPrice();
        dietsIncluded  = memberShipDTO.getDietIncluded();
        medicalValidationRequired = memberShipDTO.getNeedMedicalValidation();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public Integer getDurationInMonths() { return durationInMonths; }
    public void setDurationInMonths(int durationInMonths) { this.durationInMonths = durationInMonths; }

    public boolean getDietsIncluded() { return dietsIncluded; }
    public  void setDietsIncluded(boolean dietsIncluded) { this.dietsIncluded = dietsIncluded; }

    public void setMedicalValidationRequired(boolean isRequired) { medicalValidationRequired = isRequired; }
    public boolean getMedicalValidationRequired() { return medicalValidationRequired; }
}