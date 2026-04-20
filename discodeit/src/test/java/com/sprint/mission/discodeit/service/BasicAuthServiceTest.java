package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.InvalidCredentialsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicAuthService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicAuthServiceTest {

  @InjectMocks
  private BasicAuthService authService;

  @Mock
  private UserRepository userRepository;
  @Mock
  private UserMapper userMapper;

  @Test
  @DisplayName("로그인 성공")
  void login_success() {
    // given
    LoginRequest request = new LoginRequest("testuser", "password123");
    User user = new User("testuser", "test@test.com", "password123", null);
    UserDto expectedDto = new UserDto(UUID.randomUUID(), "testuser", "test@test.com", null, true);

    given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));
    given(userMapper.toDto(user)).willReturn(expectedDto);

    // when
    UserDto result = authService.login(request);

    // then
    assertThat(result).isNotNull();
    assertThat(result.username()).isEqualTo("testuser");
  }

  @Test
  @DisplayName("존재하지 않는 사용자 로그인 실패")
  void login_fail_userNotFound() {
    // given
    LoginRequest request = new LoginRequest("nobody", "password123");
    given(userRepository.findByUsername("nobody")).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> authService.login(request))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("비밀번호 불일치로 로그인 실패")
  void login_fail_wrongPassword() {
    // given
    LoginRequest request = new LoginRequest("testuser", "wrongpassword");
    User user = new User("testuser", "test@test.com", "password123", null);
    given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));

    // when & then
    assertThatThrownBy(() -> authService.login(request))
        .isInstanceOf(InvalidCredentialsException.class);
  }
}
