package com.gymapp.gym_backend_service.service;

import com.gymapp.gym_backend_service.authorization.JWTHandler;
import com.gymapp.gym_backend_service.data.dto.request.register_membership.AssignCustomDietRequest;
import com.gymapp.gym_backend_service.data.dto.request.register_membership.AssignValidatorRequest;
import com.gymapp.gym_backend_service.data.dto.response.registered_membership.RegisteredMembershipInfoResponseDTO;
import com.gymapp.gym_backend_service.data.enums.RegistrationStatus;
import com.gymapp.gym_backend_service.data.enums.UserRole;
import com.gymapp.gym_backend_service.data.model.*;
import com.gymapp.gym_backend_service.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class RegisteredMembershipService {
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
    @Autowired
    private CommonService commonService;

    String STATIC_FOLDER_PATH = "src/main/resources/static";
    String FILE_SAVE_PATH = "uploads/docs/";
    String URL = "http://localhost:8080/";

    public String registerMembership(String header, Long membershipId, MultipartFile medicalDocument) {
        Long memberID = commonService.getMemberID(header);
        if(memberID == null) { throw new IllegalArgumentException("Invalid token. Kindly check token"); }

        Member member = memberRepo.findById(memberID).orElse(null);
        Membership membership = membershipRepo.findById(membershipId).orElse(null);

        if(member == null) { throw new EntityNotFoundException("Invalid member ID. Unable to find the ID"); }

        if(commonService.isMemberActive(member)) { throw new IllegalArgumentException("Unable to register. This Member has already registered"); }

        if(membership == null) { throw new EntityNotFoundException("Invalid membership ID. Membership doesn't exist"); }

        List<RegisteredMembership> prevRegMemberShip = registrationRepo.findPendingRegisteredMemberShip(memberID);

        if(!prevRegMemberShip.isEmpty()) throw new IllegalArgumentException("Unable to register membership. There are membership that are not processed. Deny those to continue with new Membership registration");

        String fileName = null;

        RegisteredMembership registration = new RegisteredMembership();
        registration.setMember(member);
        registration.setMembership(membership);
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
                    throw new IllegalArgumentException("This membership requires document. Kindly upload document");
                }
            } else if (medicalDocument != null && !medicalDocument.isEmpty()) {
                throw new IllegalArgumentException("This membership don't need document to upload");
            } else {
                registration.setStatus(RegistrationStatus.PENDING);
                RegisteredMembership regMemberships = registrationRepo.save(registration);
                var invoice = invoiceDetailRepository.save(new InvoiceDetail(regMemberships));
                return "MemberShip Registered. Invoice generated ID: "+invoice.getId();
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("There is error in uploading document. Error: "+e.getMessage());
        }
        registration.setDocumentPath(fileName);
        RegisteredMembership memberships = registrationRepo.save(registration);
        return "Membership registration done. Registration ID: " + memberships.getId();
    }

    public void assignValidator(AssignValidatorRequest request) {
        Optional<RegisteredMembership> regMembership = registrationRepo.findById(request.getRegMemberShipID());
        Optional<Trainer> validatorTrainer = trainerRepository.findById(request.getTrainerID());

        if (regMembership.isEmpty()) { throw new EntityNotFoundException("Membership registered Not found"); }

        if (regMembership.get().getStatus() != RegistrationStatus.APPLIED) { throw new IllegalArgumentException("Membership not registered. Unable to assign validator to this membership"); }
        if (!regMembership.get().getMembership().getMedicalValidationRequired()) { throw new IllegalArgumentException("This membership doesn't require validator"); }

        if (validatorTrainer.isEmpty()) { throw new EntityNotFoundException("Trainer Not found"); }

        regMembership.get().setValidator(validatorTrainer.get());
        registrationRepo.save(regMembership.get());
    }

    public void assignCustomDiet(AssignCustomDietRequest request) {
        Optional<RegisteredMembership> regMembership = registrationRepo.findById(request.getRegMemberShipID());
        Optional<CustomDietPlan> assignedDiet = customDietPlanRepository.findById(request.getCustomDietID());

        if (regMembership.isEmpty()) { throw new IllegalArgumentException("Invalid Membership"); }
        if (assignedDiet.isEmpty()) { throw new IllegalArgumentException("Invalid Diet Assigned"); }

        regMembership.get().setDietPlan(assignedDiet.get());
        regMembership.get().setStatus(RegistrationStatus.REVIEWED);
        registrationRepo.save(regMembership.get());
    }

    public RegisteredMembership registerRegisteredMemberShip(String header, Long regMemId) {
        Long memberID = commonService.getMemberID(header);
        Optional<RegisteredMembership> registeredMembership = registrationRepo.findById(regMemId);

        if(memberID == null) { throw new IllegalArgumentException("Invalid token. Kindly check token"); }
        Member member = memberRepo.findById(memberID).orElse(null);

        if (registeredMembership.isEmpty()) throw new EntityNotFoundException("Invalid Membership ID");
        if(!registeredMembership.get().getMember().equals(member)) throw new IllegalArgumentException("This user unauthorized to register this membership");

        if(registeredMembership.get().getStatus() == RegistrationStatus.REGISTERED) throw new IllegalArgumentException("Registration is already done for this membership. Unable to register again");
        if(registeredMembership.get().getStatus() == RegistrationStatus.PENDING) throw new IllegalArgumentException("Unable to register this membership. Awaiting for payment");
        if(registeredMembership.get().getStatus() != RegistrationStatus.REVIEWED && registeredMembership.get().getMembership().getMedicalValidationRequired()) {
            throw new IllegalArgumentException("This membership need validation can't register without validation");
        }
        if(registeredMembership.get().getStatus() != RegistrationStatus.REVIEWED) throw new IllegalArgumentException("Unable to proceed register this membership. Error occurred.");

        var invoice = invoiceDetailRepository.save(new InvoiceDetail(registeredMembership.get()));
        registeredMembership.get().setStatus(RegistrationStatus.PENDING);
        return registrationRepo.save(registeredMembership.get());
    }

    public List<RegisteredMembershipInfoResponseDTO> getAllMemberShipInfo() {
        List<RegisteredMembership> registeredMemberships = registrationRepo.findAll();
        if (registeredMemberships.isEmpty()) { throw new EntityNotFoundException("No Membership registered yet"); }

        return registeredMemberships.stream().map((reg) -> {
            RegisteredMembershipInfoResponseDTO regMemDTO = new RegisteredMembershipInfoResponseDTO(reg);
            regMemDTO.setUploadedDoc(reg.getDocumentPath() != null ? URL + FILE_SAVE_PATH + reg.getDocumentPath() : "-");
            return regMemDTO;
        }).toList();
    }

    public RegisteredMembership getRegMemberShipInfoByID(Long regMemID) {
        Optional<RegisteredMembership> registeredMembership = registrationRepo.findById(regMemID);
        if (registeredMembership.isEmpty()) { throw new EntityNotFoundException("Invalid Membership ID"); }

        return registeredMembership.get();
    }

    public List<RegisteredMembershipInfoResponseDTO> getMemberShipInfoByMember(String header, Long memberID) {
        Long userId = commonService.getMemberID(header);
        if(userId == null) { throw new IllegalArgumentException("Invalid token. Kindly check token"); }

        User requestedUser = userRepo.findById(userId).orElse(null);
        if(requestedUser.getUserRole() == UserRole.ADMIN) { userId = memberID; }

        List<RegisteredMembership> registrations = registrationRepo.findByMemberId(userId);
        Optional<Member> _member = memberRepo.findById(userId);

        if (_member.isEmpty()) { throw new EntityNotFoundException("Invalid Member. No valid Member found"); }
        Member member = _member.get();

        if (registrations.isEmpty()) { throw new EntityNotFoundException("No Membership registered for : " + member.getName()); }

        return registrations.stream().map((reg) -> {
            RegisteredMembershipInfoResponseDTO regMemDTO = new RegisteredMembershipInfoResponseDTO(reg);
            regMemDTO.setUploadedDoc(reg.getDocumentPath() != null ? URL + FILE_SAVE_PATH + reg.getDocumentPath() : "-");
            return regMemDTO;
        }).toList();
    }

    public List<RegisteredMembershipInfoResponseDTO> getTrainerAssignedRegsitrationInfo(String header, Long trainerID) {
        Long userId = commonService.getMemberID(header);
        Long trainerId = userId;

        if(userId == null) { throw new IllegalArgumentException("Invalid token. Kindly check token"); }

        User requestedUser = userRepo.findById(userId).orElse(null);
        if(requestedUser.getUserRole() == UserRole.ADMIN) { trainerId = trainerID; }

        List<RegisteredMembership> registrations = registrationRepo.findByValidatorId(trainerId);
        Trainer trainer = trainerRepository.findById(trainerId).orElse(null);

        if (trainer == null) { throw new EntityNotFoundException("Invalid Trainer. No valid trainer found"); }
        if (registrations.isEmpty()) { throw new EntityNotFoundException("No Validation Assigned for : " + trainer.getName()); }

        return registrations.stream().map((reg) -> {
            RegisteredMembershipInfoResponseDTO regMemDTO = new RegisteredMembershipInfoResponseDTO(reg);
            regMemDTO.setUploadedDoc(reg.getDocumentPath() != null ? URL + FILE_SAVE_PATH + reg.getDocumentPath() : "-");
            return regMemDTO;
        }).toList();
    }

    public RegisteredMembership cancelRegistration(String header, Long regMemID) {
        Long id = commonService.getMemberID(header);

        if(id == null) { throw new IllegalArgumentException("Invalid token. Kindly check token"); }

        Optional<RegisteredMembership> regMem = registrationRepo.findById(regMemID);
        if (regMem.isEmpty()) { throw new EntityNotFoundException("Invalid Membership ID"); }

        if(regMem.get().getStatus() == RegistrationStatus.REGISTERED) regMem.get().setStatus(RegistrationStatus.INACTIVE);
        else  regMem.get().setStatus(RegistrationStatus.DENIED);

        return registrationRepo.save(regMem.get());
    }

}
