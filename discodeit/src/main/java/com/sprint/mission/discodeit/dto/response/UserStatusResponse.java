package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.UserStatus;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatusResponse {
    private final UUID id;
    private final UUID userId;
    private final Instant lastLogin;
    private final String loginStatus;
    private final Instant createdAt;
    private final Instant updatedAt;

    public UserStatusResponse(UserStatus userStatus) {
        this.id = userStatus.getId();
        this.userId = userStatus.getUserId();
        this.lastLogin = userStatus.getLastLogin();
        this.loginStatus = userStatus.isLoggedIn();
        this.createdAt = userStatus.getCreatedAt();
        this.updatedAt = userStatus.getUpdatedAt();
    }

}
