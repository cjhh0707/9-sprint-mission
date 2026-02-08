package com.sprint.mission.discodeit.dto.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UserStatusCreateRequest {
    private UUID userId;

    public UserStatusCreateRequest(UUID userId) {
        this.userId = userId;
    }
}
