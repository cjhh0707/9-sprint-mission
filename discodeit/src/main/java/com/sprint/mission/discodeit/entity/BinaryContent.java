package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.time.Instant;

@Getter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;    //생성 시간
    private final String filename;  //파일 이름
    private final String contentType;
    private final Long fileSize;//파일 사이즈
    private final byte[] content;
    private UUID profileId;   //프로필 아이디 참조 필드
    private List<UUID> attachmentId;    //첨부파일 아이디 참조 필드

    public BinaryContent(String filename, String contentType, Long fileSize, byte[] content) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.filename = filename;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.content = content;
    }

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
    }

    public void setAttachmentId(List<UUID> attachmentId) {
        this.attachmentId = attachmentId;
    }
}
