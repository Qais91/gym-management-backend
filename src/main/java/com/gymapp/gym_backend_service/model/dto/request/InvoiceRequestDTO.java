package com.gymapp.gym_backend_service.model.dto.request;

public class InvoiceRequestDTO {
    private Long memberShipId;
    private String paymentMode;

    public Long getMemberShipId() {
        return memberShipId;
    }
    public void setMemberShipId(Long memberShipId) {
        this.memberShipId = memberShipId;
    }

    public String getPaymentMode() {
        return paymentMode;
    }
    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }
}
