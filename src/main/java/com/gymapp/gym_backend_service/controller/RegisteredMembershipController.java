package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.model.Member;
import com.gymapp.gym_backend_service.model.Membership;
import com.gymapp.gym_backend_service.model.RegisteredMemberships;
import com.gymapp.gym_backend_service.repository.MemberRepository;
import com.gymapp.gym_backend_service.repository.MembershipRepository;
import com.gymapp.gym_backend_service.repository.RegisteredMembershipsRepository;
import com.gymapp.gym_backend_service.model.dto.response.RegisteredMembershipInfoResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/registrations")
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

    @GetMapping("/member/{memberId}")
    public ResponseEntity<?> getMemberMembershipInfo(@RequestParam Long memberID) {
        List<RegisteredMemberships> registrations = registrationRepo.findByMemberId(memberID);

        if (registrations.isEmpty()) {
            return ResponseEntity.status(404).body("No memberships found for customer ID: " + memberID);
        }

        List<RegisteredMembershipInfoResponseDTO> result = registrations.stream().map(reg ->
                new RegisteredMembershipInfoResponseDTO(
                        reg.getMembership().getTitle(),
                        reg.getStartDate(),
                        reg.getEndDate(),
                        reg.isActive()
                )
        ).toList();

        return ResponseEntity.ok(registrations);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelRegistration(@PathVariable Long id) {
        return registrationRepo.findById(id).map(reg -> {
            reg.setActive(false);
            return ResponseEntity.ok(registrationRepo.save(reg));
        }).orElse(ResponseEntity.notFound().build());
    }
}
