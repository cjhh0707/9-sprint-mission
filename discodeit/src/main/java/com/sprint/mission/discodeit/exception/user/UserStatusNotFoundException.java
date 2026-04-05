package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class UserStatusNotFoundException extends UserException {

  public UserStatusNotFoundException(UUID id) {
    super(ErrorCode.USER_STATUS_NOT_FOUND, Map.of("userStatusId", id));
  }

  public UserStatusNotFoundException(String field, UUID id) {
    super(ErrorCode.USER_STATUS_NOT_FOUND, Map.of(field, id));
  }
}
