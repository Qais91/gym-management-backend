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
import com.gymapp.gym_backend_service.service.CommonService;
import com.gymapp.gym_backend_service.service.InvoiceDetailService;
import jakarta.persistence.EntityNotFoundException;
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
    private InvoiceDetailService service;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createInvoice(@Valid @RequestBody InvoiceRequestDTO requestDTO) {
        try {
            return ResponseEntity.ok(new InvoiceResponseDTO(service.createInvoice(requestDTO)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAllInvoices() {
        try {
            return ResponseEntity.ok(service.getAllInvoices());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{invoiceId}")
    public ResponseEntity<?> getInvoiceById(@PathVariable("invoiceId") Long invoiceId) {
        try{
            return ResponseEntity.ok(service.getInvoiceByID(invoiceId));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/member")
    public ResponseEntity<?> getInvoicesForCustomer(@RequestHeader("Authorization") String header) {
        try{
            return ResponseEntity.ok(service.getInvoiceByCustomer(header));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/member/pending")
    public ResponseEntity<?> getPendingInvoicesForCustomer(@RequestHeader("Authorization") String header) {
        try{
            return ResponseEntity.ok(service.getAllPendingInvoicesByCustomer(header));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('MEMBER')")
    @PutMapping("/deny")
    public ResponseEntity<?>  denyInvoice(@RequestHeader("Authorization") String header) {
        try {
            return ResponseEntity.ok(new InvoiceResponseDTO(service.denyInvoice(header)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('MEMBER')")
    @PutMapping("/pay")
    public ResponseEntity<?>  payInvoice(@RequestHeader("Authorization") String header) {
        try{
            return ResponseEntity.ok(new InvoiceResponseDTO(service.payInvoice(header)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", e.getMessage()));
        }
    }
}
