package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserStatusMapper userStatusMapper;

  @Override
  public UserStatusDto find(UUID userStatusId) {
    return userStatusRepository.findById(userStatusId)
        .map(userStatusMapper::toDto)
        .orElseThrow(() -> new NoSuchElementException(
            "UserStatus with id " + userStatusId + " not found"));
  }

  @Transactional
  @Override
  public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request) {
    UserStatus userStatus = userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> new NoSuchElementException(
            "UserStatus with id " + userStatusId + " not found"));
    userStatus.update(request.newLastActiveAt());
    return userStatusMapper.toDto(userStatus);
  }

  @Transactional
  @Override
  public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(() -> new NoSuchElementException(
            "UserStatus with userId " + userId + " not found"));
    userStatus.update(request.newLastActiveAt());
    return userStatusMapper.toDto(userStatus);
  }
}
