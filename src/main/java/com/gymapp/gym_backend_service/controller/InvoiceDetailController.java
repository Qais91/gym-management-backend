package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.model.InvoiceDetail;
import com.gymapp.gym_backend_service.model.RegisteredMembership;
import com.gymapp.gym_backend_service.model.dto.request.InvoiceRequestDTO;
import com.gymapp.gym_backend_service.model.dto.response.ApiResponse;
import com.gymapp.gym_backend_service.model.dto.response.invoice.InvoiceResponseDTO;
import com.gymapp.gym_backend_service.model.enums.PaymentStatus;
import com.gymapp.gym_backend_service.repository.InvoiceDetailRepository;
import com.gymapp.gym_backend_service.repository.RegisteredMembershipsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceDetailController {

    @Autowired
    private InvoiceDetailRepository invoiceRepo;
    @Autowired
    private RegisteredMembershipsRepository regMemberRepo;

    @PostMapping
    public ResponseEntity<?> createInvoice(@RequestBody InvoiceRequestDTO requestDTO) {
        RegisteredMembership membership = regMemberRepo.findById(requestDTO.getMemberShipId()).orElse(null);
        if (membership == null) { return ResponseEntity.badRequest().body("Invalid customer ID"); }

        InvoiceDetail invoice = new InvoiceDetail(membership);
        invoice.setPaymentMode(requestDTO.getPaymentMode());

        return ResponseEntity.ok(invoiceRepo.save(invoice));
    }

    @GetMapping
    public ResponseEntity<?> getAllInvoices() {
        List<InvoiceDetail> invoices = invoiceRepo.findAll();
        if(invoices.isEmpty()) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", "No Invoice found for this member")); }

        List<InvoiceResponseDTO> res = invoices.stream().map(invoice -> new InvoiceResponseDTO(invoice)).toList();
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<?> getInvoiceById(@PathVariable("invoiceId") Long invoiceId) {
        Optional<InvoiceDetail> invoice = invoiceRepo.findById(invoiceId);
        if(invoice.isEmpty()) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", "No Invoice found for this member")); }
        return ResponseEntity.ok(invoice);
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<?> getInvoicesForCustomer(@PathVariable("memberId") Long customerId) {
        List<InvoiceDetail> invoices = invoiceRepo.findByMemberId(customerId);
        if(invoices.isEmpty()) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", "No Invoice found for this member")); }
        List<InvoiceResponseDTO> res = invoices.stream().map(invoice -> new InvoiceResponseDTO(invoice)).toList();
        return ResponseEntity.ok(res);
    }

    @PutMapping("/deny/{invoiceId}")
    public ResponseEntity<?>  denyInvoice(@PathVariable("invoiceId") Long invoiceId) {
        Optional<InvoiceDetail> payInvoice = invoiceRepo.findById(invoiceId);
        if (payInvoice.isEmpty()) return ResponseEntity.badRequest().body(new ApiResponse("error", "Invalid ID"));
        if(payInvoice.get().getStatus() == PaymentStatus.PAID) return ResponseEntity.badRequest().body(new ApiResponse("error", "Invoice is been Paid unable to deny this invoice"));

        payInvoice.get().setStatus(PaymentStatus.DENIED);
        return ResponseEntity.ok(new InvoiceResponseDTO(payInvoice.get()));
    }

    @PutMapping("/pay/{invoiceId}")
    public ResponseEntity<?>  payInvoice(@PathVariable("invoiceId") Long invoiceId) {
        Optional<InvoiceDetail> payInvoice = invoiceRepo.findById(invoiceId);
        if (payInvoice.isEmpty()) return ResponseEntity.badRequest().body(new ApiResponse("error", "Invalid ID"));
        if(payInvoice.get().getStatus() == PaymentStatus.DENIED) return ResponseEntity.badRequest().body(new ApiResponse("error", "Invoice is been denied unable to pay"));
        if(payInvoice.get().getStatus() == PaymentStatus.PAID) return ResponseEntity.badRequest().body(new ApiResponse("error", "Invoice is been Paid unable to pay again"));

        payInvoice.get().setStatus(PaymentStatus.PAID);
        return ResponseEntity.ok(new InvoiceResponseDTO(payInvoice.get()));
    }

//    @PutMapping("/pay/member-ship-reg/{membershipId}")
//    public ResponseEntity<?>  payInvoiceMembershipId(@PathVariable("membershipId") Long membershipId) {
//        InvoiceDetail payInvoice = invoiceRepo.findByRegisteredMembershipId(membershipId);
//        if (payInvoice == null) return ResponseEntity.badRequest().body(new ApiResponse("error", "Invalid ID"));
//        if(payInvoice.getStatus() == PaymentStatus.DENIED) return ResponseEntity.badRequest().body(new ApiResponse("error", "Invoice is been denied unable to pay"));
//        if(payInvoice.getStatus() == PaymentStatus.PAID) return ResponseEntity.badRequest().body(new ApiResponse("error", "Invoice is been Paid unable to pay again"));
//
//        payInvoice.setStatus(PaymentStatus.PAID);
//        return ResponseEntity.ok(new InvoiceResponseDTO(payInvoice));
//    }
}
