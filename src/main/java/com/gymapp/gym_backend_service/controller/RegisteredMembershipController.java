package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.authorization.JWTHandler;
import com.gymapp.gym_backend_service.data.model.*;
import com.gymapp.gym_backend_service.data.dto.request.register_membership.AssignCustomDietRequest;
import com.gymapp.gym_backend_service.data.dto.request.register_membership.AssignValidatorRequest;
import com.gymapp.gym_backend_service.data.dto.response.ApiResponse;
import com.gymapp.gym_backend_service.data.enums.RegistrationStatus;
import com.gymapp.gym_backend_service.data.enums.UserRole;
import com.gymapp.gym_backend_service.repository.*;
import com.gymapp.gym_backend_service.data.dto.response.registered_membership.RegisteredMembershipInfoResponseDTO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(RegisteredMembershipController.class);
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
    private UserRepository userRepo;
    @Autowired
    private JWTHandler jwtHandler;

    String STATIC_FOLDER_PATH = "src/main/resources/static";
    String FILE_SAVE_PATH = "uploads/docs/";
    String URL = "http://localhost:8080/";

    boolean isMemberActive(Member member) {
        return registrationRepo.isMemberShipActive(member.getId());
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

        System.out.println("-------------->>>> "+ member.getName());
        System.out.println(isMemberActive(member));

        if(isMemberActive(member)) { return ResponseEntity.badRequest().body(new ApiResponse("error", "Unable to register. This Member has already registered")); }

        if(membership == null) { return ResponseEntity.badRequest().body(new ApiResponse("error", "Invalid membership ID. Membership doesn't exist")); }

        List<RegisteredMembership> prevRegMemberShip = registrationRepo.findPendingRegisteredMemberShip(memberID);

        if(!prevRegMemberShip.isEmpty()) return ResponseEntity.badRequest().body(new ApiResponse("error", "Unable to register membership. There are membership that are not processed. Deny those to continue with new Membership registration"));

        String fileName = null;

        RegisteredMembership registration = new RegisteredMembership();
        registration.setMemeber(member);
        registration.setMembership(membership);
        registration.setActive(true);

//        System.out.println("===========================");
//        System.out.println(medicalDocument != null);
//        System.out.println(!medicalDocument.isEmpty());
//        System.out.println("===========================");

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
                registration.setStatus(RegistrationStatus.PENDING);
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

        if (!regMembership.get().getMembership().getMedicalValidationRequired()) { return ResponseEntity.badRequest().body(new ApiResponse("error", "This membership doesn't require validator")); }
        if (regMembership.get().getStatus() != RegistrationStatus.APPLIED) { return ResponseEntity.badRequest().body(new ApiResponse("error", "Unable to assign trainer to this membership")); }

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
    public ResponseEntity<?> registeredMemberShip(@RequestHeader("Authorization") String header, @PathVariable("regMemId") Long regMemId) {
        Long memberID = getMemberID(header);
        Optional<RegisteredMembership> registeredMembership = registrationRepo.findById(regMemId);

        if(memberID == null) { return ResponseEntity.badRequest().body(new ApiResponse("error", "Invalid token. Kindly check token")); }
        Member member = memberRepo.findById(memberID).orElse(null);

        if (registeredMembership.isEmpty()) {
            return ResponseEntity.status(404).body(new ApiResponse("error", "Invalid Membership ID"));
        }

        if(!registeredMembership.get().getMember().equals(member)) return ResponseEntity.badRequest().body(new ApiResponse("error",
                "This user unauthorized to register this membership"));

        if(registeredMembership.get().getStatus() == RegistrationStatus.REGISTERED) return ResponseEntity.badRequest().body(new ApiResponse("error", "Registration is already done for this membership. Unable to register"));

        if(registeredMembership.get().getStatus() == RegistrationStatus.PENDING) return ResponseEntity.badRequest().body(new ApiResponse("error", "Unable to register this membership. Awaiting for payment"));

        if(registeredMembership.get().getStatus() != RegistrationStatus.REVIEWED && registeredMembership.get().getMembership().getMedicalValidationRequired()) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", "This membership need validation can't register without validation"));
        }

        var invoice = invoiceDetailRepository.save(new InvoiceDetail(registeredMembership.get()));
        registeredMembership.get().setStatus(RegistrationStatus.PENDING);
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

    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    @GetMapping({"/member/{memberId}", "/member"})
    public ResponseEntity<?> getMemberMembershipInfo(@RequestHeader("Authorization") String header, @PathVariable(value = "memberId", required = false) Long memberID) {
        Long userId = getMemberID(header);
        if(userId == null) { return ResponseEntity.badRequest().body(new ApiResponse("error", "Invalid token. Kindly check token")); }

        User requestedUser = userRepo.findById(userId).orElse(null);
        if(requestedUser.getUserRole() == UserRole.ADMIN) { userId = memberID; }

        List<RegisteredMembership> registrations = registrationRepo.findByMemberId(userId);
        Optional<Member> _member = memberRepo.findById(userId);

        if (_member.isEmpty()) { return ResponseEntity.status(404).body(new ApiResponse("error", "Invalid Member. No valid Member found")); }
        Member member = _member.get();

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

        User requestedUser = userRepo.findById(userId).orElse(null);
        if(requestedUser.getUserRole() == UserRole.ADMIN) { trainerId = trainerID; }

        List<RegisteredMembership> registrations = registrationRepo.findByValidatorId(trainerId);
        Trainer trainer = trainerRepository.findById(trainerId).orElse(null);

        if (trainer == null) { return ResponseEntity.status(404).body(new ApiResponse("error", "Invalid Trainer. No valid trainer found")); }
        if (registrations.isEmpty()) { return ResponseEntity.status(404).body(new ApiResponse("error", "No Validation Assigned for : " + trainer.getName())); }

        List<RegisteredMembershipInfoResponseDTO> result = registrations.stream().map((reg) -> {
            RegisteredMembershipInfoResponseDTO regMemDTO = new RegisteredMembershipInfoResponseDTO(reg);
            regMemDTO.setUploadedDoc(reg.getDocumentPath() != null ? URL + FILE_SAVE_PATH + reg.getDocumentPath() : "-");
            return regMemDTO;
        }).toList();

        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('MEMBER')")
    @DeleteMapping("/{regMemID}")
    public ResponseEntity<?> cancelRegistration(@RequestHeader("Authorization") String header, @PathVariable("regMemID") Long regMemID) {
        Long id = getMemberID(header);

        if(id == null) { return ResponseEntity.status(404).body(new ApiResponse("error", "Invalid token. Kindly check token")); }

        Optional<RegisteredMembership> regMem = registrationRepo.findById(regMemID);
        if (regMem.isEmpty()) { return ResponseEntity.status(404).body(new ApiResponse("error", "Invalid Membership ID")); }

        if(regMem.get().getStatus() == RegistrationStatus.REGISTERED) regMem.get().setStatus(RegistrationStatus.INACTIVE);
        else  regMem.get().setStatus(RegistrationStatus.DENIED);

        return ResponseEntity.ok(new RegisteredMembershipInfoResponseDTO(registrationRepo.save(regMem.get())));
    }
}
