package com.gymapp.gym_backend_service.controller;

import com.gymapp.gym_backend_service.data.enums.DietMealType;
import com.gymapp.gym_backend_service.data.model.*;
import org.springframework.stereotype.Component;

@Component
public class TestConfig {
    String loginToken;
    public User test_admin = new User(
            "test_admin",
            "test admin",
            "test_admin@mail.com",
            "98244276438947",
            "admin_pass"
    );

    public Member test_member = new Member(
            "test_user",
            "test user",
            "test_user@mail.com",
            "561651212789",
            "user_pass"
    );

    public Trainer test_trainer = new Trainer(
            "test_trainer",
            "test trainer",
            "test_trainer@mail.com",
            "12132378794",
            "trainer_pass",
            "ZUMBA",
            3
    );

    public Diets test_diet1 = new Diets(
            "test meal",
            DietMealType.BREAKFAST,
            200,
            50
    );

    public Diets test_diet2 = new Diets(
            "test meal",
            DietMealType.BREAKFAST,
            200,
            50
    );

    public Membership test_regular = new Membership(
            "Regular Plan",
            1,
            800,
            false,
            false
    );

    public Membership test_premium = new Membership(
            "Premium Plan",
            1,
            1500,
            true,
            false
    );

    public Membership test_elite = new Membership(
            "Premium Plan",
            1,
            1500,
            true,
            true
    );

    public TestConfig() {
        test_diet1.setId(1L);
        test_diet2.setId(2L);
    }

    public void setLoginToken(String token) {
        loginToken = token;
    }

    public String getLoginToken() {
        return loginToken;
    }
}
