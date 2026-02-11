package com.sprint.mission.discodeit.dto.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ChannelUpdateRequest {
    private UUID channelId;
    private String channelName;
    private String description;

    public ChannelUpdateRequest(UUID channelId, String channelName, String description) {
        this.channelId = channelId;
        this.channelName = channelName;
        this.description = description;
    }
}
