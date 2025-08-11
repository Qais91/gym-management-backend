package com.gymapp.gym_backend_service.model;

import jakarta.persistence.*;

@Entity
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Integer durationInMonths;
    private double discountOnExtraMonth;
    private double pricePerDurationMonth;
    private boolean dietsIncluded = false;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public void setPlanName(String planName) { this.title = planName; }

    public double getDiscountOnExtraMonth() { return discountOnExtraMonth; }
    public void setDiscountOnExtraMonth(double discount) { this.discountOnExtraMonth = discount; }

    public double getPricePerDurationMonth() { return pricePerDurationMonth; }
    public void setPricePerDurationMonth(double price) { this.pricePerDurationMonth = price; }

    public Integer getDurationInMonths() { return durationInMonths; }
    public void setDurationInMonths(int durationInMonths) { this.durationInMonths = durationInMonths; }

    public boolean getDietsIncluded() { return dietsIncluded; }
    public  void setDietsIncluded(boolean dietsIncluded) { this.dietsIncluded = dietsIncluded; }
}