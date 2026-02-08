package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Message extends BaseEntity {
    private String content;
    private final UUID authorId;
    private final UUID channelId;
    private List<UUID> attachmentIds;

    public Message(String content, UUID authorId, UUID channelId) {
        super();
        this.content = content;
        this.authorId = authorId;
        this.channelId = channelId;
        this.attachmentIds = new ArrayList<>();
    }

//    public String getContent() {
//        return content;
//    }
//
//    public UUID getAuthorId() {
//        return authorId;
//    }
//
//    public UUID getChannelId() {
//        return channelId;
//    }

    public void update(String content) {
        this.content = content;
        setUpdatedAt();
    }

    public void setAttachmentIds(List<UUID> attachmentIds) {
        this.attachmentIds = attachmentIds;
    }
}
