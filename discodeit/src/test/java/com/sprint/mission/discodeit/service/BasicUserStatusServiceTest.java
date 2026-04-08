package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicUserStatusServiceTest {

  @InjectMocks
  private BasicUserStatusService userStatusService;

  @Mock
  private UserStatusRepository userStatusRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private UserStatusMapper userStatusMapper;

  private UserStatusDto createDto(UUID id, UUID userId) {
    return new UserStatusDto(id, userId, Instant.now());
  }

  @Test
  @DisplayName("UserStatus 생성 성공")
  void create_success() {
    // given
    UUID userId = UUID.randomUUID();
    UserStatusCreateRequest request = new UserStatusCreateRequest(userId, Instant.now());

//    User user = new User("user", "user@test.com", "pass", null);
    User user = mock(User.class);
    given(user.getStatus()).willReturn(null);

    UserStatus userStatus = new UserStatus(user, Instant.now());
    UserStatusDto expectedDto = createDto(UUID.randomUUID(), userId);

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userStatusRepository.save(any(UserStatus.class))).willReturn(userStatus);
    given(userStatusMapper.toDto(any(UserStatus.class))).willReturn(expectedDto);

    // when
    UserStatusDto result = userStatusService.create(request);

    // then
    assertThat(result).isNotNull();
    then(userStatusRepository).should().save(any(UserStatus.class));
  }

  @Test
  @DisplayName("존재하지 않는 사용자로 UserStatus 생성 실패")
  void create_fail_userNotFound() {
    // given
    UUID userId = UUID.randomUUID();
    UserStatusCreateRequest request = new UserStatusCreateRequest(userId, Instant.now());
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userStatusService.create(request))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("UserStatus 수정 성공")
  void update_success() {
    // given
    UUID statusId = UUID.randomUUID();
    UserStatusUpdateRequest request = new UserStatusUpdateRequest(Instant.now());
    User user = new User("user", "user@test.com", "pass", null);
    UserStatus userStatus = new UserStatus(user, Instant.now());
    UserStatusDto expectedDto = createDto(statusId, UUID.randomUUID());

    given(userStatusRepository.findById(statusId)).willReturn(Optional.of(userStatus));
    given(userStatusMapper.toDto(any(UserStatus.class))).willReturn(expectedDto);

    // when
    UserStatusDto result = userStatusService.update(statusId, request);

    // then
    assertThat(result).isNotNull();
  }

  @Test
  @DisplayName("존재하지 않는 UserStatus 수정 실패")
  void update_fail_notFound() {
    // given
    UUID statusId = UUID.randomUUID();
    UserStatusUpdateRequest request = new UserStatusUpdateRequest(Instant.now());
    given(userStatusRepository.findById(statusId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userStatusService.update(statusId, request))
        .isInstanceOf(UserStatusNotFoundException.class);
  }

  @Test
  @DisplayName("userId로 UserStatus 수정 성공")
  void updateByUserId_success() {
    // given
    UUID userId = UUID.randomUUID();
    UserStatusUpdateRequest request = new UserStatusUpdateRequest(Instant.now());
    User user = new User("user", "user@test.com", "pass", null);
    UserStatus userStatus = new UserStatus(user, Instant.now());
    UserStatusDto expectedDto = createDto(UUID.randomUUID(), userId);

    given(userStatusRepository.findByUserId(userId)).willReturn(Optional.of(userStatus));
    given(userStatusMapper.toDto(any(UserStatus.class))).willReturn(expectedDto);

    // when
    UserStatusDto result = userStatusService.updateByUserId(userId, request);

    // then
    assertThat(result).isNotNull();
  }

  @Test
  @DisplayName("존재하지 않는 userId로 UserStatus 수정 실패")
  void updateByUserId_fail_notFound() {
    // given
    UUID userId = UUID.randomUUID();
    UserStatusUpdateRequest request = new UserStatusUpdateRequest(Instant.now());
    given(userStatusRepository.findByUserId(userId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userStatusService.updateByUserId(userId, request))
        .isInstanceOf(UserStatusNotFoundException.class);
  }

  @Test
  @DisplayName("UserStatus 삭제 성공")
  void delete_success() {
    // given
    UUID statusId = UUID.randomUUID();
    given(userStatusRepository.existsById(statusId)).willReturn(true);

    // when
    userStatusService.delete(statusId);

    // then
    then(userStatusRepository).should().deleteById(statusId);
  }

  @Test
  @DisplayName("존재하지 않는 UserStatus 삭제 실패")
  void delete_fail_notFound() {
    // given
    UUID statusId = UUID.randomUUID();
    given(userStatusRepository.existsById(statusId)).willReturn(false);

    // when & then
    assertThatThrownBy(() -> userStatusService.delete(statusId))
        .isInstanceOf(UserStatusNotFoundException.class);
  }

  @Test
  @DisplayName("전체 UserStatus 조회 성공")
  void findAll_success() {
    // given
    given(userStatusRepository.findAll()).willReturn(List.of());

    // when
    List<UserStatusDto> result = userStatusService.findAll();

    // then
    assertThat(result).isEmpty();
  }
}
