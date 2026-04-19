package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID;

public class UserStatusNotFoundException extends UserException {

  public UserStatusNotFoundException(UUID id) {
    super(ErrorCode.USER_STATUS_NOT_FOUND);
    addDetail("userStatusId", id);
  }

  public UserStatusNotFoundException(String field, UUID id) {
    super(ErrorCode.USER_STATUS_NOT_FOUND);
    addDetail(field, id);
  }
}
