package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.Message;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class MessageResponse {
    private final UUID id;
    private final String content;
    private final UUID authorId;
    private final UUID channelId;
    private final List<UUID> attachmentIds;
    private final Instant createdAt;
    private final Instant updatedAt;

    public MessageResponse(Message message) {
        this.id = message.getId();
        this.content = message.getContent();
        this.authorId = message.getAuthorId();
        this.channelId = message.getChannelId();
        this.attachmentIds = message.getAttachmentIds();
        this.createdAt = message.getCreatedAt();
        this.updatedAt = message.getUpdatedAt();
    }
}
