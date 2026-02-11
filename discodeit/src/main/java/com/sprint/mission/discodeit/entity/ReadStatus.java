package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus extends BaseEntity {
    private final UUID userId;
    private final UUID channelId;
    private Instant lastRead;

    public ReadStatus(UUID userId, UUID channelId) {
        super();
        this.userId = userId;
        this.channelId = channelId;
        this.lastRead = Instant.now();
    }

    public void updateLastRead(Instant lastRead) {
        this.lastRead = lastRead;
        setUpdatedAt();
    }
}
