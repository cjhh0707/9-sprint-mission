package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.BinaryContent;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class BinaryContentResponse {
    private final UUID id;
    private final String filename;
    private final String contentType;
    private final Long fileSize;
    private final byte[] content;
    private final UUID profileId;
    private final List<UUID> attachmentId;
    private final Instant createdAt;

    public BinaryContentResponse(BinaryContent binaryContent) {
        this.id = binaryContent.getId();
        this.filename = binaryContent.getFilename();
        this.contentType = binaryContent.getContentType();
        this.fileSize = binaryContent.getFileSize();
        this.content = binaryContent.getContent();
        this.profileId = binaryContent.getProfileId();
        this.attachmentId = binaryContent.getAttachmentId();
        this.createdAt = binaryContent.getCreatedAt();
    }
}
