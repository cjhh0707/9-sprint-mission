package com.sprint.mission.discodeit.dto.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UserUpdateRequest {
    private UUID userId;

    private String displayName;
    private String email;
    private String password;
    private String status;

    private BinaryContentRequest profileImage;

    public UserUpdateRequest(UUID userId, String displayName, String email, String password, String status) {
        this.userId = userId;
        this.displayName = displayName;
        this.email = email;
        this.password = password;
        this.status = status;
    }

    public UserUpdateRequest(UUID userId, String displayName, String email, String password, String status, BinaryContentRequest profileImage) {
        this.userId = userId;
        this.displayName = displayName;
        this.email = email;
        this.password = password;
        this.status = status;
        this.profileImage = profileImage;
    }
}
