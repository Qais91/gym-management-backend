package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.authorization.JWTHandler;
import com.gymapp.gym_backend_service.data.enums.RegistrationStatus;
import com.gymapp.gym_backend_service.data.model.InvoiceDetail;
import com.gymapp.gym_backend_service.data.model.RegisteredMembership;
import com.gymapp.gym_backend_service.data.dto.request.invoice.InvoiceRequestDTO;
import com.gymapp.gym_backend_service.data.dto.response.ApiResponse;
import com.gymapp.gym_backend_service.data.dto.response.invoice.InvoiceResponseDTO;
import com.gymapp.gym_backend_service.data.enums.PaymentStatus;
import com.gymapp.gym_backend_service.repository.InvoiceDetailRepository;
import com.gymapp.gym_backend_service.repository.RegisteredMembershipsRepository;
import jakarta.validation.Valid;
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
    public ResponseEntity<?> createInvoice(@Valid @RequestBody InvoiceRequestDTO requestDTO) {
        RegisteredMembership membership = regMemberRepo.findById(requestDTO.getMemberShipId()).orElse(null);
        InvoiceDetail invoiceDetail = invoiceRepo.findByRegisteredMembershipId(requestDTO.getMemberShipId());

        if (membership == null) { return ResponseEntity.badRequest().body(new ApiResponse("error", "Invalid Membership ID")); }
        if(membership.getStatus() == RegistrationStatus.DENIED || membership.getStatus() == RegistrationStatus.INACTIVE) return ResponseEntity.badRequest().body(new ApiResponse("error", "Unable to initiate payment for this membership. Membership Invalidated"));

        if(invoiceDetail.getStatus() != PaymentStatus.DENIED) { return ResponseEntity.badRequest().body(new ApiResponse("error", "Unable to initiate payment for this membership")); }

        InvoiceDetail invoice = new InvoiceDetail(membership);

        return ResponseEntity.ok(new InvoiceResponseDTO(invoiceRepo.save(invoice)));
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
    @GetMapping("/member/pending")
    public ResponseEntity<?> getPendingInvoicesForCustomer(@RequestHeader("Authorization") String header) {
        Long customerId = getMemberID(header);
        List<InvoiceDetail> invoices = invoiceRepo.findPendingInvoicesByMember(customerId);

        if(invoices.isEmpty()) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", "No pending Invoices found")); }
        List<InvoiceResponseDTO> res = invoices.stream().map(invoice -> new InvoiceResponseDTO(invoice)).toList();

        return ResponseEntity.ok(res);
    }

    @PreAuthorize("hasRole('MEMBER')")
    @PutMapping("/deny")
    public ResponseEntity<?>  denyInvoice(@RequestHeader("Authorization") String header) {
        Long memberId = getMemberID(header);
        List<InvoiceDetail> payInvoices = invoiceRepo.findPendingInvoicesByMember(memberId);

//        System.out.println("MEmeber ID "+memberId);

        if(payInvoices.stream().count() == 0) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", "No Invoice pending to be paid"));

        InvoiceDetail payInvoice = payInvoices.get(0);

//        Optional<InvoiceDetail> payInvoice = invoiceRepo.findById(invoiceId);
//        if (payInvoice.isEmpty()) return ResponseEntity.status(404).body(new ApiResponse("error", "Invalid ID"));

        if(payInvoice.getStatus() == PaymentStatus.PAID) return ResponseEntity.badRequest().body(new ApiResponse("error", "Invoice is been Paid unable to deny this invoice"));

        if(!payInvoice.getMember().getId().equals(memberId)) return ResponseEntity.badRequest().body(new ApiResponse("error", "Unable to deny. UnAuthorised Access"));

        payInvoice.setStatus(PaymentStatus.DENIED);
        InvoiceDetail deniedInvoice = invoiceRepo.save(payInvoice);
        return ResponseEntity.ok(new InvoiceResponseDTO(deniedInvoice));
    }

//    @PathVariable("invoiceId") Long invoiceId
    @PreAuthorize("hasRole('MEMBER')")
    @PutMapping("/pay")
    public ResponseEntity<?>  payInvoice(@RequestHeader("Authorization") String header) {
        Long memberId = getMemberID(header);
        List<InvoiceDetail> payInvoices = invoiceRepo.findPendingInvoicesByMember(memberId);

        if(payInvoices.stream().count() == 0) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", "No Invoice pending to pay"));

        InvoiceDetail payInvoice = payInvoices.get(0);
        RegisteredMembership registeredMembership = payInvoice.getRegisteredMembership();

        if(payInvoice == null) return ResponseEntity.badRequest().body(new ApiResponse("error", "Invalid ID"));
        if(payInvoice.getStatus() == PaymentStatus.DENIED) return ResponseEntity.badRequest().body(new ApiResponse("error", "Invoice is been denied unable to pay"));
        if(payInvoice.getStatus() == PaymentStatus.PAID) return ResponseEntity.badRequest().body(new ApiResponse("error", "Invoice is been Paid unable to pay again"));

        registeredMembership.setStatus(RegistrationStatus.REGISTERED);
        payInvoice.setStatus(PaymentStatus.PAID);
        payInvoice.getRegisteredMembership().setStartDate(LocalDate.now());
        payInvoice.getRegisteredMembership().setEndDate(LocalDate.now().plusMonths(payInvoice.getRegisteredMembership().getMembership().getDurationInMonths()));
        regMemberRepo.save(registeredMembership);
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
