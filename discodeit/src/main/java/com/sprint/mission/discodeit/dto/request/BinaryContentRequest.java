package com.sprint.mission.discodeit.dto.request;

import lombok.Getter;

@Getter
public class BinaryContentRequest {
    private String filename;
    private String contentType;
    private Long fileSize;
    private byte[] content;

    public BinaryContentRequest(String filename, String contentType, Long fileSize, byte[] content) {
        this.filename = filename;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.content = content;
    }
}
