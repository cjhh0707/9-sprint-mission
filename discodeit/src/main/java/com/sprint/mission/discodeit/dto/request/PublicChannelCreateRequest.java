package com.sprint.mission.discodeit.dto.request;

import lombok.Getter;

@Getter
public class PublicChannelCreateRequest {
    private String channelName;
    private String description;

    public PublicChannelCreateRequest(String channelName, String description) {
        this.channelName = channelName;
        this.description = description;
    }
}
