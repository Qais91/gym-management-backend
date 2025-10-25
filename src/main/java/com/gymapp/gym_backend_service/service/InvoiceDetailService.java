package com.gymapp.gym_backend_service.service;

import com.gymapp.gym_backend_service.authorization.JWTHandler;
import com.gymapp.gym_backend_service.data.dto.request.invoice.InvoiceRequestDTO;
import com.gymapp.gym_backend_service.data.dto.response.ApiResponse;
import com.gymapp.gym_backend_service.data.dto.response.invoice.InvoiceResponseDTO;
import com.gymapp.gym_backend_service.data.enums.PaymentStatus;
import com.gymapp.gym_backend_service.data.enums.RegistrationStatus;
import com.gymapp.gym_backend_service.data.model.InvoiceDetail;
import com.gymapp.gym_backend_service.data.model.RegisteredMembership;
import com.gymapp.gym_backend_service.repository.InvoiceDetailRepository;
import com.gymapp.gym_backend_service.repository.RegisteredMembershipsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceDetailService {
    @Autowired
    private InvoiceDetailRepository invoiceRepo;
    @Autowired
    private RegisteredMembershipsRepository regMemberRepo;
    @Autowired
    private CommonService commonService;

    public InvoiceDetail createInvoice(InvoiceRequestDTO requestDTO) {
        RegisteredMembership membership = regMemberRepo.findById(requestDTO.getMemberShipId()).orElse(null);
        InvoiceDetail invoiceDetail = invoiceRepo.findByRegisteredMembershipId(requestDTO.getMemberShipId());

        if (membership == null) { throw new IllegalArgumentException("Invalid Membership ID"); }
        if(membership.getStatus() == RegistrationStatus.DENIED || membership.getStatus() == RegistrationStatus.INACTIVE) throw new IllegalArgumentException("Unable to initiate payment for this membership. Membership Invalidated");

        if(invoiceDetail.getStatus() != PaymentStatus.DENIED) { throw new IllegalArgumentException("Unable to initiate payment for this membership"); }

        InvoiceDetail invoice = new InvoiceDetail(membership);
        return invoiceRepo.save(invoice);
    }

    public List<InvoiceResponseDTO> getAllInvoices() {
        List<InvoiceDetail> invoices = invoiceRepo.findAll();

        if(invoices.isEmpty()) { throw new EntityNotFoundException("No Invoice found for this member"); }

        return invoices.stream().map(invoice -> new InvoiceResponseDTO(invoice)).toList();
    }

    public InvoiceDetail getInvoiceByID(Long invoiceID) {
        Optional<InvoiceDetail> invoice = invoiceRepo.findById(invoiceID);

        if(invoice.isEmpty()) { throw new EntityNotFoundException("No Invoice found for this member"); }

        return invoice.get();
    }

    public List<InvoiceResponseDTO> getInvoiceByCustomer(String header) {
        Long customerId = commonService.getMemberID(header);
        List<InvoiceDetail> invoices = invoiceRepo.findByMemberId(customerId);

        if(invoices.isEmpty()) { throw new EntityNotFoundException("No Invoice found for this member"); }

        return invoices.stream().map(invoice -> new InvoiceResponseDTO(invoice)).toList();
    }

    public List<InvoiceResponseDTO> getAllPendingInvoicesByCustomer(String header) {
        Long customerId = commonService.getMemberID(header);
        List<InvoiceDetail> invoices = invoiceRepo.findPendingInvoicesByMember(customerId);

        if(invoices.isEmpty()) { throw new EntityNotFoundException("No pending Invoices found"); }

        return invoices.stream().map(invoice -> new InvoiceResponseDTO(invoice)).toList();
    }

    public InvoiceDetail denyInvoice(String header) {
        Long memberId = commonService. getMemberID(header);
        List<InvoiceDetail> payInvoices = invoiceRepo.findPendingInvoicesByMember(memberId);

        if(payInvoices.stream().count() == 0) throw new EntityNotFoundException("No Invoice pending to be paid");

        InvoiceDetail payInvoice = payInvoices.get(0);

        if(payInvoice.getStatus() == PaymentStatus.PAID) throw new IllegalArgumentException("Invoice is been Paid unable to deny this invoice");

        if(!payInvoice.getMember().getId().equals(memberId)) throw new IllegalArgumentException("Unable to deny. UnAuthorised Access");

        payInvoice.setStatus(PaymentStatus.DENIED);
        return invoiceRepo.save(payInvoice);
    }

    public InvoiceDetail payInvoice(String header) {
        Long memberId = commonService.getMemberID(header);
        List<InvoiceDetail> payInvoices = invoiceRepo.findPendingInvoicesByMember(memberId);

        if(payInvoices.stream().count() == 0) throw new EntityNotFoundException("No Invoice pending to pay");

        InvoiceDetail payInvoice = payInvoices.get(0);
        RegisteredMembership registeredMembership = payInvoice.getRegisteredMembership();

        if(payInvoice.getStatus() == PaymentStatus.DENIED) throw new IllegalArgumentException("Invoice is been denied unable to pay");
        if(payInvoice.getStatus() == PaymentStatus.PAID) throw new IllegalArgumentException("Invoice is been Paid unable to pay again");

        registeredMembership.setStatus(RegistrationStatus.REGISTERED);
        payInvoice.setStatus(PaymentStatus.PAID);
        payInvoice.getRegisteredMembership().setStartDate(LocalDate.now());
        payInvoice.getRegisteredMembership().setEndDate(LocalDate.now().plusMonths(payInvoice.getRegisteredMembership().getMembership().getDurationInMonths()));
        regMemberRepo.save(registeredMembership);
        return invoiceRepo.save(payInvoice);
    }
}
