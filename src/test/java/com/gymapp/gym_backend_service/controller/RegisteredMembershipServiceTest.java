package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.data.dto.request.register_membership.AssignValidatorRequest;
import com.gymapp.gym_backend_service.data.enums.RegistrationStatus;
import com.gymapp.gym_backend_service.data.model.InvoiceDetail;
import com.gymapp.gym_backend_service.data.model.RegisteredMembership;
import com.gymapp.gym_backend_service.repository.*;
import com.gymapp.gym_backend_service.service.CommonService;
import com.gymapp.gym_backend_service.service.RegisteredMembershipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.*;

@ExtendWith(MockitoExtension.class)
public class RegisteredMembershipServiceTest {
    TestConfig testingConfig;
    RegisteredMembership unRegisteredMembership;
    @Mock
    CommonService commonService;
    @Mock
    MembershipRepository membershipRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    RegisteredMembershipsRepository registeredMembershipsRepository;
    @Mock
    InvoiceDetailRepository invoiceDetailRepository;
    @Mock
    TrainerRepository trainerRepository;

    @InjectMocks
    RegisteredMembershipService registeredMembershipService;

    @BeforeEach
    void setUp() {
        testingConfig = new TestConfig();
    }

    @Test
    void testRegularMembershipRegistration() {
        unRegisteredMembership = new RegisteredMembership();
        unRegisteredMembership.setId(1L);
        unRegisteredMembership.setMembership(testingConfig.test_regular);
        unRegisteredMembership.setMember(testingConfig.test_member);
        unRegisteredMembership.setStatus(RegistrationStatus.PENDING);

        assertNotNull("Membership not assigned", unRegisteredMembership.getMembership());

        when(commonService.getMemberID(any(String.class))).thenReturn(999L);
        when(memberRepository.findById(anyLong())).thenReturn(Optional.ofNullable(testingConfig.test_member));
        when(membershipRepository.findById(anyLong())).thenReturn(Optional.ofNullable(unRegisteredMembership.getMembership()));
        when(registeredMembershipsRepository.findPendingRegisteredMemberShip(anyLong())).thenReturn(Arrays.asList());
        when(registeredMembershipsRepository.save(any(RegisteredMembership.class))).thenReturn(unRegisteredMembership);

        InvoiceDetail demo_invoice = new InvoiceDetail(unRegisteredMembership);
        when(invoiceDetailRepository.save(any(InvoiceDetail.class))).thenReturn(demo_invoice);

        String res = registeredMembershipService.registerMembership("token", 1L, null);
        assertTrue("Issue with membership register logic", res.contains("MemberShip Registered"));
    }

    @Test
    void testEliteMembershipRegistration() {
        unRegisteredMembership = new RegisteredMembership();
        unRegisteredMembership.setId(2L);
        unRegisteredMembership.setMembership(testingConfig.test_elite);
        unRegisteredMembership.setMember(testingConfig.test_member);
        unRegisteredMembership.setStatus(RegistrationStatus.PENDING);

        assertNotNull("Membership not assigned", unRegisteredMembership.getMembership());

        when(commonService.getMemberID(any(String.class))).thenReturn(999L);
        when(memberRepository.findById(anyLong())).thenReturn(Optional.ofNullable(testingConfig.test_member));
        when(membershipRepository.findById(anyLong())).thenReturn(Optional.ofNullable(unRegisteredMembership.getMembership()));
        when(registeredMembershipsRepository.findPendingRegisteredMemberShip(anyLong())).thenReturn(Arrays.asList());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> registeredMembershipService.registerMembership("token", 1L, null));
        assertTrue("Elite membership document validation check failed", exception.getMessage().contains("This membership requires document. Kindly upload document"));
    }

    @Test
    void testValidatorAssignment() {
        unRegisteredMembership = new RegisteredMembership();
        unRegisteredMembership.setId(2L);
        unRegisteredMembership.setMembership(testingConfig.test_regular);
        unRegisteredMembership.setMember(testingConfig.test_member);
        unRegisteredMembership.setStatus(RegistrationStatus.PENDING);

        when(registeredMembershipsRepository.findById(anyLong())).thenReturn(Optional.ofNullable(unRegisteredMembership));
        when(trainerRepository.findById(anyLong())).thenReturn(Optional.ofNullable(testingConfig.test_trainer));

        Exception res = assertThrows(IllegalArgumentException.class, () -> registeredMembershipService.assignValidator(new AssignValidatorRequest(1L, 2L)));
        assertTrue("Validator assignment on unregistered membership test failed", res.getMessage().contains("Membership not registered"));

        unRegisteredMembership.setStatus(RegistrationStatus.APPLIED);

        Exception res1 = assertThrows(IllegalArgumentException.class, () -> registeredMembershipService.assignValidator(new AssignValidatorRequest(1L, 2L)));
        assertTrue("Validator assignment on unregistered membership test failed", res1.getMessage().contains("This membership doesn't require validator"));

        unRegisteredMembership.setMembership(testingConfig.test_elite);

        assertDoesNotThrow(() -> registeredMembershipService.assignValidator(new AssignValidatorRequest(1L, 2L)));
    }

    @Test
    void testRegisterEliteMembershipApplied() {
        unRegisteredMembership = new RegisteredMembership();
        unRegisteredMembership.setId(2L);
        unRegisteredMembership.setMembership(testingConfig.test_elite);
        unRegisteredMembership.setMember(testingConfig.test_member);
        unRegisteredMembership.setStatus(RegistrationStatus.REGISTERED);

        when(commonService.getMemberID(any(String.class))).thenReturn(999L);
        when(registeredMembershipsRepository.findById(anyLong())).thenReturn(Optional.ofNullable(unRegisteredMembership));
        when(memberRepository.findById(anyLong())).thenReturn(Optional.ofNullable(testingConfig.test_member));

        Exception res1 = assertThrows(IllegalArgumentException.class, () -> registeredMembershipService.registerRegisteredMemberShip("token", 1L));
        assertTrue("Registering applied Membership test failed 1", res1.getMessage().contains("Registration is already done for this membership. Unable to register again"));

        unRegisteredMembership.setStatus(RegistrationStatus.APPLIED);
        res1 = assertThrows(IllegalArgumentException.class, () -> registeredMembershipService.registerRegisteredMemberShip("token", 1L));
        assertTrue("Registering applied Membership test failed 2", res1.getMessage().contains("This membership need validation can't register without validation"));

        unRegisteredMembership.setStatus(RegistrationStatus.REVIEWED);
        assertDoesNotThrow(() -> registeredMembershipService.registerRegisteredMemberShip("token", 1L));
    }

}
