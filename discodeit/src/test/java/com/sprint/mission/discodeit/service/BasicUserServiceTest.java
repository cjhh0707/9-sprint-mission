package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.ArgumentMatchers.any;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BasicUserServiceTest {

  @InjectMocks
  private BasicUserService userService;

  @Mock
  private UserRepository userRepository;
  @Mock
  private org.springframework.security.core.session.SessionRegistry sessionRegistry;
  @Mock
  private UserMapper userMapper;
  @Mock
  private BinaryContentRepository binaryContentRepository;
  @Mock
  private BinaryContentStorage binaryContentStorage;
  @Mock
  private PasswordEncoder passwordEncoder;

  private User createMockUser(String username, String email) {
    return new User(username, email, "password123", null);
  }

  private UserDto createMockUserDto(UUID id, String username, String email) {
    return new UserDto(id, username, email, null, true, Role.USER);
  }

  // ======================== create 테스트 ========================

  @Test
  @DisplayName("사용자 생성 성공")
  void create_success() {
    // given
    UserCreateRequest request = new UserCreateRequest("testuser", "test@example.com", "password123");
    User user = createMockUser("testuser", "test@example.com");
    UUID userId = UUID.randomUUID();
    UserDto expectedDto = createMockUserDto(userId, "testuser", "test@example.com");

    given(userRepository.existsByEmail("test@example.com")).willReturn(false);
    given(userRepository.existsByUsername("testuser")).willReturn(false);
    given(userRepository.save(any(User.class))).willReturn(user);
    given(userMapper.toDto(any(User.class))).willReturn(expectedDto);

    // when
    UserDto result = userService.create(request, Optional.empty());

    // then
    assertThat(result).isNotNull();
    assertThat(result.username()).isEqualTo("testuser");
    assertThat(result.email()).isEqualTo("test@example.com");
    then(userRepository).should().save(any(User.class));
  }

  @Test
  @DisplayName("이메일 중복으로 사용자 생성 실패")
  void create_fail_duplicateEmail() {
    // given
    UserCreateRequest request = new UserCreateRequest("testuser", "test@example.com", "password123");
    given(userRepository.existsByEmail("test@example.com")).willReturn(true);

    // when & then
    assertThatThrownBy(() -> userService.create(request, Optional.empty()))
        .isInstanceOf(UserAlreadyExistsException.class);
  }

  @Test
  @DisplayName("사용자명 중복으로 사용자 생성 실패")
  void create_fail_duplicateUsername() {
    // given
    UserCreateRequest request = new UserCreateRequest("testuser", "test@example.com", "password123");
    given(userRepository.existsByEmail("test@example.com")).willReturn(false);
    given(userRepository.existsByUsername("testuser")).willReturn(true);

    // when & then
    assertThatThrownBy(() -> userService.create(request, Optional.empty()))
        .isInstanceOf(UserAlreadyExistsException.class);
  }

  // ======================== update 테스트 ========================

  @Test
  @DisplayName("사용자 수정 성공")
  void update_success() {
    // given
    UUID userId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("newname", "new@example.com", "newpassword");
    User user = createMockUser("testuser", "test@example.com");
    UserDto expectedDto = createMockUserDto(userId, "newname", "new@example.com");

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userRepository.existsByEmail("new@example.com")).willReturn(false);
    given(userRepository.existsByUsername("newname")).willReturn(false);
    given(userMapper.toDto(any(User.class))).willReturn(expectedDto);

    // when
    UserDto result = userService.update(userId, request, Optional.empty());

    // then
    assertThat(result).isNotNull();
    assertThat(result.username()).isEqualTo("newname");
  }

  @Test
  @DisplayName("존재하지 않는 사용자 수정 실패")
  void update_fail_userNotFound() {
    // given
    UUID userId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("newname", "new@example.com", "newpassword");
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userService.update(userId, request, Optional.empty()))
        .isInstanceOf(UserNotFoundException.class);
  }

  // ======================== delete 테스트 ========================

  @Test
  @DisplayName("사용자 삭제 성공")
  void delete_success() {
    // given
    UUID userId = UUID.randomUUID();
    given(userRepository.existsById(userId)).willReturn(true);

    // when
    userService.delete(userId);

    // then
    then(userRepository).should().deleteById(userId);
  }

  @Test
  @DisplayName("존재하지 않는 사용자 삭제 실패")
  void delete_fail_userNotFound() {
    // given
    UUID userId = UUID.randomUUID();
    given(userRepository.existsById(userId)).willReturn(false);

    // when & then
    assertThatThrownBy(() -> userService.delete(userId))
        .isInstanceOf(UserNotFoundException.class);
  }
}
