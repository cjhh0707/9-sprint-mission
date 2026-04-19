package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidPasswordException extends UserException {

  public InvalidPasswordException(String username) {
    super(ErrorCode.INVALID_PASSWORD);
    addDetail("username", username);
  }
}
