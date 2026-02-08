package com.sprint.mission.discodeit.dto.request;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatusUpdateRequest {
    private UUID readStatusId;
    private Instant lastRead;

    public ReadStatusUpdateRequest(UUID readStatusId, Instant lastRead) {
        this.readStatusId = readStatusId;
        this.lastRead = lastRead;
    }
}
