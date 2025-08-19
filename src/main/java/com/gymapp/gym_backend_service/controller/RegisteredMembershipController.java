package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.model.*;
import com.gymapp.gym_backend_service.model.dto.request.register_membership.AssignCustomDietRequest;
import com.gymapp.gym_backend_service.model.dto.request.register_membership.AssignValidatorRequest;
import com.gymapp.gym_backend_service.model.dto.response.ApiResponse;
import com.gymapp.gym_backend_service.model.enums.RegistrationStatus;
import com.gymapp.gym_backend_service.repository.*;
import com.gymapp.gym_backend_service.model.dto.response.registered_membership.RegisteredMembershipInfoResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/registered-membership")
public class RegisteredMembershipController {

    @Autowired
    private RegisteredMembershipsRepository registrationRepo;
    @Autowired
    private MemberRepository memberRepo;
    @Autowired
    private MembershipRepository membershipRepo;
    @Autowired
    private TrainerRepository trainerRepository;
    @Autowired
    private CustomDietPlanRepository customDietPlanRepository;
    @Autowired
    private InvoiceDetailRepository invoiceDetailRepository;

    String static_folder_path = "src/main/resources/static";
    String file_save_path = "uploads/docs/";
    String url = "http://localhost:8080/";

    @PostMapping
    public ResponseEntity<?> registerMembership(@RequestParam Long memberId, @RequestParam Long membershipId, @RequestParam(required = false) MultipartFile medicalDocument) {
        Member member = memberRepo.findById(memberId).orElse(null);
        Membership membership = membershipRepo.findById(membershipId).orElse(null);

        if (member == null) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", "Invalid member"));
        }

        if(membership == null) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", "Invalid membership ID"));
        }
;
        String fileName = null;

        RegisteredMembership registration = new RegisteredMembership();
        registration.setMemeber(member);
        registration.setMembership(membership);
        registration.setStartDate(LocalDate.now());
        registration.setEndDate(LocalDate.now().plusMonths(membership.getDurationInMonths()));
        registration.setActive(true);

        try {
            if (membership.getMedicalValidationRequired()) {
                if (medicalDocument != null && !medicalDocument.isEmpty()) {

                    String uploadDir = System.getProperty("user.dir") + File.separator + static_folder_path + File.separator + file_save_path;

                    File dir = new File(uploadDir);
                    if (!dir.exists()) dir.mkdirs();

                    fileName = System.currentTimeMillis() + "_" + medicalDocument.getOriginalFilename();
                    medicalDocument.transferTo(new File(dir, fileName));
                } else {
                   return ResponseEntity.badRequest().body(new ApiResponse("error", "This membership requires document. Kindly upload document"));
                }
            } else if (medicalDocument != null && !medicalDocument.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse("error", "This membership don't need document to uplaod"));
            } else {
                RegisteredMembership memberships = registrationRepo.save(registration);
                return ResponseEntity.ok(new ApiResponse("sucess", "Membership registration done. ID : " + memberships.getId()));
            }
        } catch (IOException e) {
            System.out.println("ERROR OCCURED ::");
            System.out.println(e);
            return ResponseEntity.ok(new ApiResponse("error", "There is error in uploading document. Error: "+e.getMessage()));
        }
        registration.setDocumentPath(fileName);
        RegisteredMembership memberships = registrationRepo.save(registration);
        return ResponseEntity.ok(new ApiResponse("sucess", "Membership registration done. ID : " + memberships.getId()));
    }

    @PutMapping("/assign/validator")
    public ResponseEntity<?> assignValidator(@Valid @RequestBody AssignValidatorRequest request) {
        Optional<RegisteredMembership> regMembership = registrationRepo.findById(request.getRegMemberShipID());
        Optional<Trainer> validatorTrainer = trainerRepository.findById(request.getTrainerID());

        if (regMembership.isEmpty()) { return ResponseEntity.badRequest().body(new ApiResponse("error", "Membership registered Not found")); }
        if (validatorTrainer.isEmpty()) { return ResponseEntity.badRequest().body(new ApiResponse("error", "Trainer Not found")); }

        regMembership.get().setValidator(validatorTrainer.get());
        registrationRepo.save(regMembership.get());

        return ResponseEntity.ok(new ApiResponse("success", "Validator Updated."));
    }

    @PutMapping("/assign/custom-diet")
    public ResponseEntity<?> assignCustomDiet(@Valid @RequestBody AssignCustomDietRequest request) {
        Optional<RegisteredMembership> regMembership = registrationRepo.findById(request.getRegMemberShipID());
        Optional<CustomDietPlan> assignedDiet = customDietPlanRepository.findById(request.getCustomDietID());

        if (regMembership.isEmpty()) { return ResponseEntity.badRequest().body(new ApiResponse("error", "Invalid Membership")); }
        if (assignedDiet.isEmpty()) { return ResponseEntity.badRequest().body(new ApiResponse("error", "Invalid Diet Assigned")); }

        regMembership.get().setDietPlan(assignedDiet.get());
        regMembership.get().setStatus(RegistrationStatus.REVIEWED);
        registrationRepo.save(regMembership.get());

        return ResponseEntity.ok(new ApiResponse("success", "Diet Updated"));
    }

    @PutMapping("/register/{regMemId}")
    public ResponseEntity<?> getRegisteredMemberShip(@PathVariable("regMemId") Long regMemId) {
        Optional<RegisteredMembership> registeredMembership = registrationRepo.findById(regMemId);
        if (registeredMembership.isEmpty()) {
            return ResponseEntity.status(404).body(new ApiResponse("error", "Invalid Membership ID"));
        }

        System.out.println("STATUS :: " + registeredMembership.get().getStatus());

        if(registeredMembership.get().getStatus() == RegistrationStatus.REGISTERED) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", "Registration done for this membership"));
        }

        if(registeredMembership.get().getStatus() != RegistrationStatus.REVIEWED && registeredMembership.get().getMembership().getMedicalValidationRequired()) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", "This membership need validation can't register without validation"));
        }

        var invoice = invoiceDetailRepository.save(new InvoiceDetail(registeredMembership.get()));
        registeredMembership.get().setStatus(RegistrationStatus.REGISTERED);
        registrationRepo.save(registeredMembership.get());
        return ResponseEntity.ok(new ApiResponse("success", "MemberShip Registered. Invoice generated ID: "+invoice.getId()));
    }

    @GetMapping
    public ResponseEntity<?> getAllMemberShipInfo() {
        List<RegisteredMembership> registeredMemberships = registrationRepo.findAll();
        if (registeredMemberships.isEmpty()) {
            return ResponseEntity.status(404).body(new ApiResponse("error", "No Membership registered yet"));
        }

        List<RegisteredMembershipInfoResponseDTO> result = registeredMemberships.stream().map((reg) -> {
            RegisteredMembershipInfoResponseDTO regMemDTO = new RegisteredMembershipInfoResponseDTO(reg);
            regMemDTO.setUploadedDoc(reg.getDocumentPath() != null ? url + file_save_path + reg.getDocumentPath() : "_");
            return regMemDTO;
        }).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{regMemId}")
    public ResponseEntity<?> getRegMemberShipInfoByID(@PathVariable("regMemId") Long regMemID) {
        Optional<RegisteredMembership> registeredMembership = registrationRepo.findById(regMemID);
        if (registeredMembership.isEmpty()) {
            return ResponseEntity.status(404).body(new ApiResponse("error", "Invalid Membership ID"));
        }

        return ResponseEntity.ok(new RegisteredMembershipInfoResponseDTO(registeredMembership.get()));
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<?> getMemberMembershipInfo(@PathVariable("memberId") Long memberId) {
        List<RegisteredMembership> registrations = registrationRepo.findByMemberId(memberId);
         Member member = memberRepo.findById(memberId).get();

        if (registrations.isEmpty()) {
            return ResponseEntity.status(404).body(new ApiResponse("error", "No Membership registered for : " + member.getName()));
        }

        List<RegisteredMembershipInfoResponseDTO> result = registrations.stream().map((reg) -> {
            RegisteredMembershipInfoResponseDTO regMemDTO = new RegisteredMembershipInfoResponseDTO(reg);
            regMemDTO.setUploadedDoc(reg.getDocumentPath() != null ? url + file_save_path + reg.getDocumentPath() : "_");
            return regMemDTO;
        }).toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<?> getTrainerAssignedRegistratrionInfo(@PathVariable("trainerId") Long trainerId) {
        List<RegisteredMembership> registrations = registrationRepo.findByValidatorId(trainerId);
        Trainer trainer = trainerRepository.findById(trainerId).get();

        if (registrations.isEmpty()) {
            return ResponseEntity.status(404).body(new ApiResponse("error", "No Membership registered for : " + trainer.getName()));
        }

        List<RegisteredMembershipInfoResponseDTO> result = registrations.stream().map((reg) -> {
            RegisteredMembershipInfoResponseDTO regMemDTO = new RegisteredMembershipInfoResponseDTO(reg);
            regMemDTO.setUploadedDoc(reg.getDocumentPath() != null ? url + file_save_path + reg.getDocumentPath() : "_");
            return regMemDTO;
        }).toList();

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelRegistration(@PathVariable Long id) {
        return registrationRepo.findById(id).map(reg -> {
            reg.setActive(false);
            return ResponseEntity.ok(registrationRepo.save(reg));
        }).orElse(ResponseEntity.notFound().build());
    }
}
