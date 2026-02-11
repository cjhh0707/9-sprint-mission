package com.sprint.mission.discodeit.dto.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class MessageUpdateRequest {
    private UUID messageId;
    private String content;

    public MessageUpdateRequest(UUID messageId, String content) {
        this.messageId = messageId;
        this.content = content;
    }
}
