package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.authorization.JWTHandler;
import com.gymapp.gym_backend_service.data.model.InvoiceDetail;
import com.gymapp.gym_backend_service.data.model.RegisteredMembership;
import com.gymapp.gym_backend_service.data.dto.request.InvoiceRequestDTO;
import com.gymapp.gym_backend_service.data.dto.response.ApiResponse;
import com.gymapp.gym_backend_service.data.dto.response.invoice.InvoiceResponseDTO;
import com.gymapp.gym_backend_service.data.enums.PaymentStatus;
import com.gymapp.gym_backend_service.repository.InvoiceDetailRepository;
import com.gymapp.gym_backend_service.repository.RegisteredMembershipsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceDetailController {

    @Autowired
    private InvoiceDetailRepository invoiceRepo;
    @Autowired
    private RegisteredMembershipsRepository regMemberRepo;
    @Autowired
    private JWTHandler jwtHandler;

    Long getMemberID(String header) {
        String token = header.substring(7);
        return jwtHandler.extractUserId(token);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createInvoice(@RequestBody InvoiceRequestDTO requestDTO) {
        RegisteredMembership membership = regMemberRepo.findById(requestDTO.getMemberShipId()).orElse(null);
        if (membership == null) { return ResponseEntity.badRequest().body("Invalid customer ID"); }

        InvoiceDetail invoice = new InvoiceDetail(membership);
        invoice.setPaymentMode(requestDTO.getPaymentMode());

        return ResponseEntity.ok(invoiceRepo.save(invoice));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAllInvoices() {
        List<InvoiceDetail> invoices = invoiceRepo.findAll();
        if(invoices.isEmpty()) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", "No Invoice found for this member")); }

        List<InvoiceResponseDTO> res = invoices.stream().map(invoice -> new InvoiceResponseDTO(invoice)).toList();
        return ResponseEntity.ok(res);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{invoiceId}")
    public ResponseEntity<?> getInvoiceById(@PathVariable("invoiceId") Long invoiceId) {
        Optional<InvoiceDetail> invoice = invoiceRepo.findById(invoiceId);
        if(invoice.isEmpty()) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", "No Invoice found for this member")); }
        return ResponseEntity.ok(invoice);
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/member")
    public ResponseEntity<?> getInvoicesForCustomer(@RequestHeader("Authorization") String header) {
        Long customerId = getMemberID(header);
        List<InvoiceDetail> invoices = invoiceRepo.findByMemberId(customerId);

        if(invoices.isEmpty()) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", "No Invoice found for this member")); }
        List<InvoiceResponseDTO> res = invoices.stream().map(invoice -> new InvoiceResponseDTO(invoice)).toList();

        return ResponseEntity.ok(res);
    }

    @PreAuthorize("hasRole('MEMBER')")
    @PutMapping("/deny/{invoiceId}")
    public ResponseEntity<?>  denyInvoice(@RequestHeader("Authorization") String header, @PathVariable("invoiceId") Long invoiceId) {
        Long memberId = getMemberID(header);
        Optional<InvoiceDetail> payInvoice = invoiceRepo.findById(invoiceId);
        if (payInvoice.isEmpty()) return ResponseEntity.status(404).body(new ApiResponse("error", "Invalid ID"));

        if(payInvoice.get().getStatus() == PaymentStatus.PAID) return ResponseEntity.badRequest().body(new ApiResponse("error", "Invoice is been Paid unable to deny this invoice"));

        if(!payInvoice.get().getMember().getId().equals(memberId)) return ResponseEntity.badRequest().body(new ApiResponse("error", "Unable to deny. UnAuthorised Access"));

        payInvoice.get().setStatus(PaymentStatus.DENIED);
        return ResponseEntity.ok(new InvoiceResponseDTO(payInvoice.get()));
    }

    @PutMapping("/pay/{invoiceId}")
    public ResponseEntity<?>  payInvoice(@PathVariable("invoiceId") Long invoiceId) {
        InvoiceDetail payInvoice = invoiceRepo.findById(invoiceId).orElse(null);
        if (payInvoice == null) return ResponseEntity.badRequest().body(new ApiResponse("error", "Invalid ID"));
        if(payInvoice.getStatus() == PaymentStatus.DENIED) return ResponseEntity.badRequest().body(new ApiResponse("error", "Invoice is been denied unable to pay"));
        if(payInvoice.getStatus() == PaymentStatus.PAID) return ResponseEntity.badRequest().body(new ApiResponse("error", "Invoice is been Paid unable to pay again"));

        payInvoice.setStatus(PaymentStatus.PAID);
        payInvoice.getRegisteredMembership().setStartDate(LocalDate.now());
        payInvoice.getRegisteredMembership().setEndDate(LocalDate.now().plusMonths(payInvoice.getRegisteredMembership().getMembership().getDurationInMonths()));
        InvoiceDetail invoice = invoiceRepo.save(payInvoice);
        return ResponseEntity.ok(new InvoiceResponseDTO(invoice));
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
