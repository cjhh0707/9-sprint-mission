package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class ChannelResponse {
    private final UUID id;
    private final String channelName;
    private final String description;
    private final ChannelType type;

    private final Instant lastMessageAt;

    private final List<UUID> participantUserIds;

    private final Instant createdAt;
    private final Instant updatedAt;

    public ChannelResponse(Channel channel, Instant lastMessageAt) {
        this.id = channel.getId();
        this.channelName = channel.getChannelName();
        this.description = channel.getDescription();
        this.type = channel.getType();
        this.lastMessageAt = lastMessageAt;
        this.participantUserIds = null;
        this.createdAt = channel.getCreatedAt();
        this.updatedAt = channel.getUpdatedAt();
    }

    public ChannelResponse(Channel channel, Instant lastMessageAt, List<UUID> participantUserIds) {
        this.id = channel.getId();
        this.channelName = channel.getChannelName();
        this.description = channel.getDescription();
        this.type = channel.getType();
        this.lastMessageAt = lastMessageAt;
        this.participantUserIds = participantUserIds;
        this.createdAt = channel.getCreatedAt();
        this.updatedAt = channel.getUpdatedAt();
    }
}
