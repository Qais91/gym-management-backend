package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.authorization.JWTHandler;
import com.gymapp.gym_backend_service.model.*;
import com.gymapp.gym_backend_service.model.dto.request.register_membership.AssignCustomDietRequest;
import com.gymapp.gym_backend_service.model.dto.request.register_membership.AssignValidatorRequest;
import com.gymapp.gym_backend_service.model.dto.response.ApiResponse;
import com.gymapp.gym_backend_service.model.enums.RegistrationStatus;
import com.gymapp.gym_backend_service.model.enums.UserRole;
import com.gymapp.gym_backend_service.repository.*;
import com.gymapp.gym_backend_service.model.dto.response.registered_membership.RegisteredMembershipInfoResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/register-membership")
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
    @Autowired
    private RegisteredMembershipsRepository registeredMembershipsRepository;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private JWTHandler jwtHandler;

    String STATIC_FOLDER_PATH = "src/main/resources/static";
    String FILE_SAVE_PATH = "uploads/docs/";
    String URL = "http://localhost:8080/";

    boolean isMemberActive(Member member) {
        Optional<RegisteredMembership> memReg = registeredMembershipsRepository.findByIdAndEndDateAfter(member.getId(), LocalDate.now());
        return !memReg.isEmpty();
    }

    Long getMemberID(String header) {
        String token = header.substring(7);
        return jwtHandler.extractUserId(token);
    }

    @PreAuthorize("hasRole('MEMBER')")
    @PostMapping
    public ResponseEntity<?> registerMembership(@RequestHeader("Authorization") String header, @RequestParam Long membershipId, @RequestParam(required = false) MultipartFile medicalDocument) {
//        Optional<Member> member = memberRepo.findById(memberId);
        Long memberID = getMemberID(header);
        if(memberID == null) { return ResponseEntity.badRequest().body(new ApiResponse("error", "Invalid token. Kindly check token")); }

        Member member = memberRepo.findById(memberID).orElse(null);
        Membership membership = membershipRepo.findById(membershipId).orElse(null);

        if(isMemberActive(member)) { return ResponseEntity.badRequest().body(new ApiResponse("error", "Unable to register. This Member has already registered")); }

        if(membership == null) { return ResponseEntity.badRequest().body(new ApiResponse("error", "Invalid membership ID. Membership doesn't exist")); }

        String fileName = null;

        RegisteredMembership registration = new RegisteredMembership();
        registration.setMemeber(member);
        registration.setMembership(membership);
//        registration.setStartDate(LocalDate.now());
//        registration.setEndDate(LocalDate.now().plusMonths(membership.getDurationInMonths()));
        registration.setActive(true);

        try {
            if (membership.getMedicalValidationRequired()) {
                if (medicalDocument != null && !medicalDocument.isEmpty()) {

                    String uploadDir = System.getProperty("user.dir") + File.separator + STATIC_FOLDER_PATH + File.separator + FILE_SAVE_PATH;

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
                registration.setStatus(RegistrationStatus.REGISTERED);
                RegisteredMembership regMemberships = registrationRepo.save(registration);
                var invoice = invoiceDetailRepository.save(new InvoiceDetail(regMemberships));
                return ResponseEntity.ok(new ApiResponse("sucess", "MemberShip Registered. Invoice generated ID: "+invoice.getId()));
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

    @PreAuthorize("hasRole('ADMIN')")
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

    @PreAuthorize("hasRole('TRAINER')")
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

    @PreAuthorize("hasRole('MEMBER')")
    @PutMapping("/register/{regMemId}")
    public ResponseEntity<?> registeredMemberShip(@PathVariable("regMemId") Long regMemId) {
        Optional<RegisteredMembership> registeredMembership = registrationRepo.findById(regMemId);
        if (registeredMembership.isEmpty()) {
            return ResponseEntity.status(404).body(new ApiResponse("error", "Invalid Membership ID"));
        }

        if(registeredMembership.get().getStatus() == RegistrationStatus.REGISTERED) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", "Registration is already done for this membership"));
        }

        if(registeredMembership.get().getStatus() != RegistrationStatus.REVIEWED && registeredMembership.get().getMembership().getMedicalValidationRequired()) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", "This membership need validation can't register without validation"));
        }

        var invoice = invoiceDetailRepository.save(new InvoiceDetail(registeredMembership.get()));
        registeredMembership.get().setStatus(RegistrationStatus.REGISTERED);
        registrationRepo.save(registeredMembership.get());
        return ResponseEntity.ok(new ApiResponse("success", "MemberShip Registered. Invoice generated ID: "+invoice.getId()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAllMemberShipInfo() {
        List<RegisteredMembership> registeredMemberships = registrationRepo.findAll();
        if (registeredMemberships.isEmpty()) {
            return ResponseEntity.status(404).body(new ApiResponse("error", "No Membership registered yet"));
        }

        List<RegisteredMembershipInfoResponseDTO> result = registeredMemberships.stream().map((reg) -> {
            RegisteredMembershipInfoResponseDTO regMemDTO = new RegisteredMembershipInfoResponseDTO(reg);
            regMemDTO.setUploadedDoc(reg.getDocumentPath() != null ? URL + FILE_SAVE_PATH + reg.getDocumentPath() : "-");
            return regMemDTO;
        }).toList();
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{regMemId}")
    public ResponseEntity<?> getRegMemberShipInfoByID(@PathVariable("regMemId") Long regMemID) {
        Optional<RegisteredMembership> registeredMembership = registrationRepo.findById(regMemID);
        if (registeredMembership.isEmpty()) {
            return ResponseEntity.status(404).body(new ApiResponse("error", "Invalid Membership ID"));
        }

        return ResponseEntity.ok(new RegisteredMembershipInfoResponseDTO(registeredMembership.get()));
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/member")
    public ResponseEntity<?> getMemberMembershipInfo(@RequestHeader("Authorization") String header) {
        Long memberId = getMemberID(header);
        if(memberId == null) { return ResponseEntity.badRequest().body(new ApiResponse("error", "Invalid token. Kindly check token")); }

        List<RegisteredMembership> registrations = registrationRepo.findByMemberId(memberId);
         Member member = memberRepo.findById(memberId).get();

        if (registrations.isEmpty()) {
            return ResponseEntity.status(404).body(new ApiResponse("error", "No Membership registered for : " + member.getName()));
        }

        List<RegisteredMembershipInfoResponseDTO> result = registrations.stream().map((reg) -> {
            RegisteredMembershipInfoResponseDTO regMemDTO = new RegisteredMembershipInfoResponseDTO(reg);
            regMemDTO.setUploadedDoc(reg.getDocumentPath() != null ? URL + FILE_SAVE_PATH + reg.getDocumentPath() : "-");
            return regMemDTO;
        }).toList();

        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    @GetMapping({"/trainer/{trainerId}", "/trainer"})
    public ResponseEntity<?> getTrainerAssignedRegistratrionInfo(@RequestHeader("Authorization") String header, @PathVariable(value = "trainerId", required = false) Long trainerID) {
        Long userId = getMemberID(header);
        Long trainerId = userId;

        if(userId == null) { return ResponseEntity.status(404).body(new ApiResponse("error", "Invalid token. Kindly check token")); }
        System.out.print("---->> USER " + userId);

        User requestedUser = userRepo.findById(userId).orElse(null);
        if(requestedUser.getUserRole() == UserRole.ADMIN) { trainerId = trainerID; }
        System.out.print("---->> USER IS NOT ADMIN");

        List<RegisteredMembership> registrations = registrationRepo.findByValidatorId(trainerId);
        Trainer trainer = trainerRepository.findById(trainerId).orElse(null);

        if (trainer == null) { return ResponseEntity.status(404).body(new ApiResponse("error", "Invalid Trainer. No valid trainer found")); }
        if (registrations.isEmpty()) { return ResponseEntity.status(404).body(new ApiResponse("error", "No Membership Assigned for : " + trainer.getName())); }

        List<RegisteredMembershipInfoResponseDTO> result = registrations.stream().map((reg) -> {
            RegisteredMembershipInfoResponseDTO regMemDTO = new RegisteredMembershipInfoResponseDTO(reg);
            regMemDTO.setUploadedDoc(reg.getDocumentPath() != null ? URL + FILE_SAVE_PATH + reg.getDocumentPath() : "-");
            return regMemDTO;
        }).toList();

        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('MEMBER')")
    @DeleteMapping
    public ResponseEntity<?> cancelRegistration(@RequestHeader("Authorization") String header) {
        Long id = getMemberID(header);

        if(id == null) { return ResponseEntity.status(404).body(new ApiResponse("error", "Invalid token. Kindly check token")); }

        return registrationRepo.findById(id).map(reg -> {
            reg.setActive(false);
            return ResponseEntity.ok(registrationRepo.save(reg));
        }).orElse(ResponseEntity.notFound().build());
    }
}
