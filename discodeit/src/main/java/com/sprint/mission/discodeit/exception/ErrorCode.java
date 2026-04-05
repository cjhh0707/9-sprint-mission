package com.sprint.mission.discodeit.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

  // User
  USER_NOT_FOUND("User not found", HttpStatus.NOT_FOUND),
  DUPLICATE_EMAIL("Email already exists", HttpStatus.CONFLICT),
  DUPLICATE_USERNAME("Username already exists", HttpStatus.CONFLICT),
  INVALID_PASSWORD("Invalid password", HttpStatus.UNAUTHORIZED),
  USER_STATUS_NOT_FOUND("UserStatus not found", HttpStatus.NOT_FOUND),
  DUPLICATE_USER_STATUS("UserStatus already exists", HttpStatus.CONFLICT),

  // Channel
  CHANNEL_NOT_FOUND("Channel not found", HttpStatus.NOT_FOUND),
  PRIVATE_CHANNEL_UPDATE("Private channel cannot be updated", HttpStatus.BAD_REQUEST),

  // Message
  MESSAGE_NOT_FOUND("Message not found", HttpStatus.NOT_FOUND),

  // BinaryContent
  BINARY_CONTENT_NOT_FOUND("BinaryContent not found", HttpStatus.NOT_FOUND),
  BINARY_CONTENT_DUPLICATE("BinaryContent already exists", HttpStatus.CONFLICT);

  private final String message;
  private final HttpStatus httpStatus;

  ErrorCode(String message, HttpStatus httpStatus) {
    this.message = message;
    this.httpStatus = httpStatus;
  }

  public String getMessage() {
    return message;
  }

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }
}
