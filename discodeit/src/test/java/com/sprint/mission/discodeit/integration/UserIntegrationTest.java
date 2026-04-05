package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  private MockMultipartFile buildUserCreatePart(UserCreateRequest request) throws Exception {
    return new MockMultipartFile(
        "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );
  }

  @Test
  @DisplayName("사용자 생성 통합 테스트 - 성공")
  void createUser_success() throws Exception {
    UserCreateRequest request = new UserCreateRequest("integrationuser", "integration@test.com", "password123");

    mockMvc.perform(multipart("/api/users")
            .file(buildUserCreatePart(request))
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("integrationuser"))
        .andExpect(jsonPath("$.email").value("integration@test.com"))
        .andExpect(jsonPath("$.id").isNotEmpty());
  }

  @Test
  @DisplayName("중복 이메일로 사용자 생성 실패 - 409 반환")
  void createUser_fail_duplicateEmail() throws Exception {
    UserCreateRequest first = new UserCreateRequest("user1", "dup@test.com", "password123");
    UserCreateRequest second = new UserCreateRequest("user2", "dup@test.com", "password456");

    mockMvc.perform(multipart("/api/users")
            .file(buildUserCreatePart(first))
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated());

    mockMvc.perform(multipart("/api/users")
            .file(buildUserCreatePart(second))
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("DUPLICATE_EMAIL"));
  }

  @Test
  @DisplayName("전체 사용자 목록 조회 통합 테스트")
  void findAllUsers_success() throws Exception {
    UserCreateRequest request = new UserCreateRequest("listuser", "list@test.com", "password123");

    mockMvc.perform(multipart("/api/users")
            .file(buildUserCreatePart(request))
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated());

    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  @Test
  @DisplayName("사용자 삭제 통합 테스트 - 성공")
  void deleteUser_success() throws Exception {
    UserCreateRequest request = new UserCreateRequest("deleteuser", "delete@test.com", "password123");

    MvcResult result = mockMvc.perform(multipart("/api/users")
            .file(buildUserCreatePart(request))
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andReturn();

    String responseBody = result.getResponse().getContentAsString();
    String userId = objectMapper.readTree(responseBody).get("id").asText();

    mockMvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNoContent());

    assertThat(userRepository.existsById(java.util.UUID.fromString(userId))).isFalse();
  }
}
