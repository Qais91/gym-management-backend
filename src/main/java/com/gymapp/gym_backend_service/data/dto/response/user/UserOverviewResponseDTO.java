package com.gymapp.gym_backend_service.data.dto.response.user;

import com.gymapp.gym_backend_service.data.model.User;

public class UserOverviewResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;

    public UserOverviewResponseDTO(User data) {
        id = data.getId();
        name = data.getName();
        email = data.getEmail();
        phoneNumber = data.getPhoneNumber();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
}
