package com.sprint.mission.discodeit.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.config.TestSecurityConfig;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import com.sprint.mission.discodeit.service.UserService;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private LoginSuccessHandler loginSuccessHandler;

  @MockitoBean
  private LoginFailureHandler loginFailureHandler;

  @MockitoBean
  private DiscodeitUserDetailsService userDetailsService;

  @MockitoBean
  private UserService userService;

  @Test
  @DisplayName("CSRF 토큰 발급 - 203 응답")
  void getCsrfToken_Success() throws Exception {
    mockMvc.perform(get("/api/auth/csrf-token"))
        .andExpect(status().is(203));
  }

  @Test
  @DisplayName("인증된 사용자 정보 조회 성공")
  void me_Success() throws Exception {
    UUID userId = UUID.randomUUID();
    UserDto userDto = new UserDto(userId, "testuser", "test@example.com", null, true, Role.USER);
    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, "encodedPassword");

    mockMvc.perform(get("/api/auth/me")
            .with(user(userDetails)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.username").value("testuser"))
        .andExpect(jsonPath("$.email").value("test@example.com"));
  }
}