package com.sprint.mission.discodeit.dto.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ReadStatusCreateRequest {
    private UUID userId;
    private UUID channelId;

    public ReadStatusCreateRequest(UUID userId, UUID channelId) {
        this.userId = userId;
        this.channelId = channelId;
    }
}
