package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import java.util.UUID;

public interface UserStatusService {

  UserStatusDto find(UUID userStatusId);

  UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request);

  UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request);
}
