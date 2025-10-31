package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.data.dto.request.CreateDietPlanRequestDTO;
import com.gymapp.gym_backend_service.data.dto.request.custom_diet.UpdateDietPlanRequestDTO;
import com.gymapp.gym_backend_service.data.dto.response.CustomDietPlanResponseDTO;
import com.gymapp.gym_backend_service.data.enums.DietMealType;
import com.gymapp.gym_backend_service.data.model.CustomDietPlan;
import com.gymapp.gym_backend_service.data.model.Diets;
import com.gymapp.gym_backend_service.repository.CustomDietPlanRepository;
import com.gymapp.gym_backend_service.repository.DietsRepository;
import com.gymapp.gym_backend_service.repository.TrainerRepository;
import com.gymapp.gym_backend_service.repository.UserRepository;
import com.gymapp.gym_backend_service.service.AuthService;
import com.gymapp.gym_backend_service.service.CommonService;
import com.gymapp.gym_backend_service.service.CustomDietPlanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.*;

@ExtendWith(MockitoExtension.class)
public class CustomDietServiceTest {

    TestConfig testingConfig;

    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private CommonService commonService;
    @Mock
    private DietsRepository dietsRepository;
    @Mock
    private CustomDietPlanRepository customDietPlanRepository;

    @InjectMocks
    private AuthService authService;
    @InjectMocks
    private CustomDietPlanService customDietPlanService;

    @BeforeEach
    void setUp() {
        testingConfig = new TestConfig();
    }

    @Test
    void testCreateCustomDietPlan() {
        CreateDietPlanRequestDTO req = new CreateDietPlanRequestDTO();
        req.setTitle("test meal");
        req.setDietsList(Arrays.asList(1, 2));

        when(commonService.getMemberID(any(String.class))).thenReturn(999L);
        when(trainerRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(testingConfig.test_trainer));
        when(dietsRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(testingConfig.test_diet1));
        when(customDietPlanRepository.save(any(CustomDietPlan.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        CustomDietPlan res = customDietPlanService.createCustomDietPlan("token", req);

        assertNotNull("Result is null it should not be null", res);
        assertTrue("Result should contains test diet", res.getDiets().contains(testingConfig.test_diet1));
    }

    @Test
    void updateDietPlan() {
        CustomDietPlan dietPlan = new CustomDietPlan();
        dietPlan.setDiets(new ArrayList<>(Arrays.asList(testingConfig.test_diet1, testingConfig.test_diet2)));
        dietPlan.setTitle("demo diet");
        dietPlan.setCreatedBy(testingConfig.test_trainer);

        Diets test_diet = new Diets(
                "test meal 3",
                DietMealType.LUNCH,
                300,
                50
        );
        test_diet.setId(3L);

        when(dietsRepository.findAllById(any())).thenReturn(Arrays.asList(test_diet));
        when(customDietPlanRepository.findById(any(Long.class))).thenReturn(Optional.of(dietPlan));
        when(customDietPlanRepository.save(any(CustomDietPlan.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        UpdateDietPlanRequestDTO updateReq = new UpdateDietPlanRequestDTO();
        updateReq.setDietPlanId(888L);
        updateReq.setNewTitle("DEMO Diet");
        updateReq.setDietIdsToAdd(Arrays.asList(3L));
        updateReq.setDietIdsToRemove(Arrays.asList(1L));

        CustomDietPlanResponseDTO res = customDietPlanService.updateDietPlan(updateReq);

        assertFalse("Test Diet 1 should be removed", res.getDiets().stream().anyMatch(dietSummaryDTO -> Objects.equals(dietSummaryDTO.getId(), testingConfig.test_diet1.getId())));
        assertTrue("Test Diet 2 should not be removed", res.getDiets().stream().anyMatch(dietSummaryDTO -> Objects.equals(dietSummaryDTO.getId(), testingConfig.test_diet2.getId())));
        assertTrue("Test Diet 3 should be added", res.getDiets().stream().anyMatch(dietSummaryDTO -> Objects.equals(dietSummaryDTO.getId(), test_diet.getId())));
    }
}
