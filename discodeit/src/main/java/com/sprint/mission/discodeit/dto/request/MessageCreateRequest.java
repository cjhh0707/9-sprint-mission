package com.sprint.mission.discodeit.dto.request;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class MessageCreateRequest {
    private String content;
    private UUID authorId;
    private UUID channelId;
    private List<BinaryContentRequest> attachments;

    public MessageCreateRequest(String content, UUID authorId, UUID channelId) {
        this.content = content;
        this.authorId = authorId;
        this.channelId = channelId;
    }

    public MessageCreateRequest(String content, UUID authorId, UUID channelId, List<BinaryContentRequest> attachments) {
        this.content = content;
        this.authorId = authorId;
        this.channelId = channelId;
        this.attachments = attachments;
    }
}
