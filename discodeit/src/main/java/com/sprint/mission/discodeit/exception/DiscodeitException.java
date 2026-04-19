package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class DiscodeitException extends RuntimeException {
    private final Instant timestamp;
    private final ErrorCode errorCode;
    private final List<ExceptionDetail> details;

    public DiscodeitException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = new ArrayList<>();
    }

    public DiscodeitException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = new ArrayList<>();
    }

    public void addDetail(String key, Object value) {
        this.details.add(ExceptionDetail.of(key, value));
    }
}
