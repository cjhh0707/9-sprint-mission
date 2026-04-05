package com.sprint.mission.discodeit.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private UserStatusService userStatusService;

  private UserDto createUserDto(UUID id, String username, String email) {
    return new UserDto(id, username, email, null, true);
  }

  @Test
  @DisplayName("사용자 생성 성공 - 201 반환")
  void create_success() throws Exception {
    UUID userId = UUID.randomUUID();
    UserCreateRequest request = new UserCreateRequest("testuser", "test@example.com", "password123");
    UserDto response = createUserDto(userId, "testuser", "test@example.com");

    given(userService.create(any(), any())).willReturn(response);

    MockMultipartFile userCreateRequestPart = new MockMultipartFile(
        "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    mockMvc.perform(multipart("/api/users")
            .file(userCreateRequestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.username").value("testuser"))
        .andExpect(jsonPath("$.email").value("test@example.com"));
  }

  @Test
  @DisplayName("사용자 생성 실패 - 유효성 검사 오류 400 반환")
  void create_fail_validation() throws Exception {
    UserCreateRequest request = new UserCreateRequest("", "invalid-email", "123");

    MockMultipartFile userCreateRequestPart = new MockMultipartFile(
        "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    mockMvc.perform(multipart("/api/users")
            .file(userCreateRequestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("사용자 전체 조회 성공 - 200 반환")
  void findAll_success() throws Exception {
    List<UserDto> users = List.of(
        createUserDto(UUID.randomUUID(), "user1", "user1@test.com"),
        createUserDto(UUID.randomUUID(), "user2", "user2@test.com")
    );

    given(userService.findAll()).willReturn(users);

    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].username").value("user1"));
  }

  @Test
  @DisplayName("사용자 삭제 성공 - 204 반환")
  void delete_success() throws Exception {
    UUID userId = UUID.randomUUID();
    willDoNothing().given(userService).delete(userId);

    mockMvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("존재하지 않는 사용자 삭제 - 404 반환")
  void delete_fail_notFound() throws Exception {
    UUID userId = UUID.randomUUID();
    willThrow(new UserNotFoundException(userId)).given(userService).delete(userId);

    mockMvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
  }
}
