package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
    Instant timestamp,
    String code,
    String message,
    Map<String, Object> details,
    String exceptionType,
    int status
) {

  public static ErrorResponse of(DiscodeitException e) {
    return new ErrorResponse(
        e.getTimestamp(),
        e.getErrorCode().name(),
        e.getMessage(),
        e.getDetails(),
        e.getClass().getSimpleName(),
        e.getErrorCode().getHttpStatus().value()
    );
  }

  public static ErrorResponse of(Instant timestamp, String code, String message,
      Map<String, Object> details, String exceptionType, int status) {
    return new ErrorResponse(timestamp, code, message, details, exceptionType, status);
  }
}
