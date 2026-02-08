package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserResponse {
    private final UUID id;
    private final String displayName;
    private final String email;
    private final String status;

    private final String loginStatus;
    private final Instant lastLogin;

    private final Instant createdAt;
    private final Instant updatedAt;

    public UserResponse(User user, UserStatus userStatus) {
        this.id = user.getId();
        this.displayName = user.getDisplayName();
        this.email = user.getEmail();
        this.status = user.getStatus();

        if (userStatus != null) {
            this.loginStatus = userStatus.isLoggedIn();
            this.lastLogin = userStatus.getLastLogin();
        } else {
            this.loginStatus = "알 수 없음";
            this.lastLogin = null;
        }

        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }



}
