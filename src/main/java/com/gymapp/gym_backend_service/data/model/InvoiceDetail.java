package com.gymapp.gym_backend_service.data.model;

import com.gymapp.gym_backend_service.data.enums.PaymentStatus;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class InvoiceDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private double amount;
    private LocalDate paymentDate;
    private String paymentMode;
    private String description;

    @ManyToOne
    @JoinColumn(name = "registered_membership_id")
    private RegisteredMembership registeredMembership;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;

    public InvoiceDetail() {}
    public InvoiceDetail(RegisteredMembership membershipReg) {
        member = membershipReg.getMember();
        amount = membershipReg.getMembership().getPrice();
        description = String.format("Payment Breakdown:\nMemebership Fee: %f\n", membershipReg.getMembership().getPrice());
        if (membershipReg.getDietPlan() != null) {
            amount += membershipReg.getDietPlan().getDietsPrice();
            description += String.format("Diets: %d", membershipReg.getDietPlan().getDietsPrice());
        }
        registeredMembership = membershipReg;
        paymentDate = LocalDate.now();
        paymentMode = "ONLINE";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }

    public RegisteredMembership getRegisteredMembership() { return registeredMembership; }
    public void setRegisteredMembership(RegisteredMembership registeredMembership) { this.registeredMembership = registeredMembership; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public void setStatus(PaymentStatus status) { this.status = status; }
    public PaymentStatus getStatus() { return status; }
}
