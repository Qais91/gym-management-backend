package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.model.InvoiceDetail;
import com.gymapp.gym_backend_service.model.Member;
import com.gymapp.gym_backend_service.model.dto.request.InvoiceRequestDTO;
import com.gymapp.gym_backend_service.repository.InvoiceDetailRepository;
import com.gymapp.gym_backend_service.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceDetailController {

    @Autowired
    private InvoiceDetailRepository invoiceRepo;
    @Autowired
    private MemberRepository memberRepo;

    @PostMapping
    public ResponseEntity<?> createInvoice(@RequestBody InvoiceRequestDTO requestDTO) {
        Member member = memberRepo.findById(requestDTO.getMemberId()).orElse(null);
        if (member == null) {
            return ResponseEntity.badRequest().body("Invalid customer ID");
        }

        InvoiceDetail invoice = new InvoiceDetail();
        invoice.setMember(member);
        invoice.setAmount(requestDTO.getAmount());
        invoice.setPaymentDate(LocalDate.now());
        invoice.setDescription(requestDTO.getDescription());
        invoice.setPaymentMode(requestDTO.getPaymentMode());

        return ResponseEntity.ok(invoiceRepo.save(invoice));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<InvoiceDetail>> getInvoicesForCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(invoiceRepo.findByMemberId(customerId));
    }
}
