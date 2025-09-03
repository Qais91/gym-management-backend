package com.gymapp.gym_backend_service.data.model;

import com.gymapp.gym_backend_service.data.dto.request.memberShip.CreateMemberShipRequestDTO;
import jakarta.persistence.*;

@Entity
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Integer durationInMonths;
    private double price;
    private boolean trainerIncluded = false;
    private boolean medicalValidationRequired = false;

    Membership() {}

    public Membership(String title, Integer durationMonth, double price, boolean trainerIncluded, boolean medicalValidationRequired) {
        this.title = title;
        this.durationInMonths = durationMonth;
        this.price = price;
        this.trainerIncluded = trainerIncluded;
        this.medicalValidationRequired = medicalValidationRequired;
    }

    public Membership(CreateMemberShipRequestDTO memberShipDTO) {
        title = memberShipDTO.getTitle();
        durationInMonths = memberShipDTO.getTimePeriodInMonth();
        price = memberShipDTO.getPrice();
        trainerIncluded = memberShipDTO.getTrainerIncluded();
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

    public boolean getTrainerIncluded() { return trainerIncluded; }
    public  void setTrainerIncluded(boolean trainerIncluded) { this.trainerIncluded = trainerIncluded; }

    public void setMedicalValidationRequired(boolean isRequired) { medicalValidationRequired = isRequired; }
    public boolean getMedicalValidationRequired() { return medicalValidationRequired; }
}