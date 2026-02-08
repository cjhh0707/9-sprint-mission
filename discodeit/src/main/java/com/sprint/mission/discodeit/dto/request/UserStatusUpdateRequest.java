package com.sprint.mission.discodeit.dto.request;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatusUpdateRequest {
    private UUID userStatusId;
    private Instant lastLogin;

    public UserStatusUpdateRequest(UUID userStatusId, Instant lastLogin) {
        this.userStatusId = userStatusId;
        this.lastLogin = lastLogin;
    }
}
