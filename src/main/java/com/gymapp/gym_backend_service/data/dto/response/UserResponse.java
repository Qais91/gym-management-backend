package com.gymapp.gym_backend_service.data.dto.response;

import com.gymapp.gym_backend_service.data.model.User;

public class UserResponse {
    private Long id;
    private String name;

    public UserResponse(User userModel) {
        this.id = userModel.getId();
        this.name = userModel.getUsername();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
}
