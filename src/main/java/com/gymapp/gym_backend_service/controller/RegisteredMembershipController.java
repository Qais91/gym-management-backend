package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.model.Member;
import com.gymapp.gym_backend_service.model.Membership;
import com.gymapp.gym_backend_service.model.RegisteredMemberships;
import com.gymapp.gym_backend_service.model.Trainer;
import com.gymapp.gym_backend_service.model.dto.response.ApiResponse;
import com.gymapp.gym_backend_service.repository.MemberRepository;
import com.gymapp.gym_backend_service.repository.MembershipRepository;
import com.gymapp.gym_backend_service.repository.RegisteredMembershipsRepository;
import com.gymapp.gym_backend_service.model.dto.response.registered_membership.RegisteredMembershipInfoResponseDTO;
import com.gymapp.gym_backend_service.repository.TrainerRepository;
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
@RequestMapping("/api/register")
public class RegisteredMembershipController {

    @Autowired
    private RegisteredMembershipsRepository registrationRepo;
    @Autowired
    private MemberRepository memberRepo;
    @Autowired
    private MembershipRepository membershipRepo;
    @Autowired
    private TrainerRepository trainerRepository;

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
            }
        } catch (IOException e) {
            System.out.println("ERROR OCCURED ::");
            System.out.println(e);
            return ResponseEntity.ok(new ApiResponse("error", "There is error in uploading document. Error: "+e.getMessage()));
        }

        RegisteredMemberships registration = new RegisteredMemberships();
        registration.setCustomer(member);
        registration.setMembership(membership);
        registration.setStartDate(LocalDate.now());
        registration.setEndDate(LocalDate.now().plusMonths(membership.getDurationInMonths()));
        registration.setDocumentPath(fileName);
        registration.setActive(true);

        RegisteredMemberships memberships = registrationRepo.save(registration);
        return ResponseEntity.ok(new ApiResponse("sucess", "Membership registration done. ID : " + memberships.getId()));
    }

    @PutMapping("/assign/validator")
    public ResponseEntity<?> assignValidator(@RequestParam Long registeredMemberShipID, @RequestParam Long trainerID) {
        Optional<RegisteredMemberships> regMembership = registrationRepo.findById(registeredMemberShipID);
        Optional<Trainer> validatorTrainer = trainerRepository.findById(trainerID);

        if (regMembership.isEmpty()) { return ResponseEntity.badRequest().body(new ApiResponse("error", "Membership registered Not found")); }
        if (validatorTrainer.isEmpty()) { return ResponseEntity.badRequest().body(new ApiResponse("error", "Trainer Not found")); }

        regMembership.get().setValidator(validatorTrainer.get());
        registrationRepo.save(regMembership.get());

        return ResponseEntity.ok(new ApiResponse("success", "Validator Updated."));
    }

//    @PutMapping("/assign/custom-diet")
//    public ResponseEntity<?> assignCustomDiet()

    @GetMapping("/member/{memberId}")
    public ResponseEntity<?> getMemberMembershipInfo(@PathVariable("memberId") Long memberId) {
        List<RegisteredMemberships> registrations = registrationRepo.findByMemberId(memberId);
         Member member = memberRepo.findById(memberId).get();

        if (registrations.isEmpty()) {
            return ResponseEntity.status(404).body(new ApiResponse("error", "No yet Membership registered for : " + member.getName()));
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
