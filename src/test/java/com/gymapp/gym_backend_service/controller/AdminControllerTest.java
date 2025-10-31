package com.gymapp.gym_backend_service.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymapp.gym_backend_service.data.model.Trainer;
import com.gymapp.gym_backend_service.data.model.User;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    TestConfig testingConfig;
    static String token;

    @Test
    @Order(1)
    void testAddAdmin() throws Exception {
        User user = testingConfig.test_admin;

        mockMvc.perform(post("/api/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(user)))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void testLogin() throws Exception {
        User user = testingConfig.test_admin;

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        token = json.get("token").asText();
    }

    @Test
    @Order(3)
    void testCreateUser() throws Exception {
        User testUser = testingConfig.test_member;
        String reqPayload = String.format("{\n" +
                "  \"name\": \"%s\",\n" +
                "  \"email\": \"%s\",\n" +
                "  \"username\": \"%s\",\n" +
                "  \"password\": \"%s\",\n" +
                "  \"phoneNumber\": \"%s\"\n" +
                "}", testUser.getName(), testUser.getEmail(), testUser.getUsername(), testUser.getPassword(), testUser.getPhoneNumber());

        String jwt = "Bearer " + token;

        mockMvc.perform(post("/api/member")
                .header("Authorization", jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqPayload)
        ).andExpect(status().isOk());
    }

    @Test
    void testAddTrainer() throws Exception {
        String jwt = "Bearer " + token;
        Trainer test_trainer = testingConfig.test_trainer;
        String reqPayload = String.format("{\n" +
                "  \"name\": \"%s\",\n" +
                "  \"email\": \"%s\",\n" +
                "  \"username\": \"%s\",\n" +
                "  \"password\": \"%s\",\n" +
                "  \"phoneNumber\": \"%s\",\n" +
                "  \"specialization\": \"%s\",\n" +
                "  \"experience\": %d\n" +
                "}", test_trainer.getName(), test_trainer.getEmail(), test_trainer.getUsername(), test_trainer.getPassword(), test_trainer.getPhoneNumber(), test_trainer.getSpecialization(), test_trainer.getExperience());

        mockMvc.perform(post("/api/trainer")
                .header("Authorization", jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqPayload)
        ).andExpect(status().isOk());
    }

    @Test
    void testViewDiets() throws Exception {
        String jwt = "Bearer " + token;
        mockMvc.perform(get("/api/diet-plans")
                .header("Authorization", jwt)
        ).andExpect(status().is(403));
    }
}
