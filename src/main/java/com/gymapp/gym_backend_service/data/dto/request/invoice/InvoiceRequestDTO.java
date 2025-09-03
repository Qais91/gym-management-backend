package com.gymapp.gym_backend_service.data.dto.request.invoice;

import jakarta.validation.constraints.NotNull;

public class InvoiceRequestDTO {
    @NotNull(message = "Membership ID is mandatory")
    private Long memberShipId;
//    @NotBlank(message = "Payment mode is ")
//    private String paymentMode;

    public Long getMemberShipId() {
        return memberShipId;
    }
    public void setMemberShipId(Long memberShipId) {
        this.memberShipId = memberShipId;
    }

//    public String getPaymentMode() {
//        return paymentMode;
//    }
//    public void setPaymentMode(String paymentMode) {
//        this.paymentMode = paymentMode;
//    }
}
