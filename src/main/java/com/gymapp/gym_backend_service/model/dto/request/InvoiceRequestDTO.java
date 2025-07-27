package com.gymapp.gym_backend_service.model.dto.request;

import com.gymapp.gym_backend_service.model.RegisteredMemberships;

public class InvoiceRequestDTO {
    private Long memberId;
    private double amount;
    private String paymentMode;
    private String description;
    private RegisteredMemberships registeredMemberShip;

    public Long getMemberId() {
        return memberId;
    }
    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public RegisteredMemberships getRegisteredMemberships() {
        return registeredMemberShip;
    }
    public void setRegisteredMemberShip(RegisteredMemberships registeredMemberShip) {
        this.registeredMemberShip = registeredMemberShip;
    }

    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentMode() {
        return paymentMode;
    }
    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

}
