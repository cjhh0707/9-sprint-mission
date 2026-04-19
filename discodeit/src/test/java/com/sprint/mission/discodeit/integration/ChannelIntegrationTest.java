package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
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
class ChannelIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private String createUserAndGetId(String username, String email) throws Exception {
    UserCreateRequest request = new UserCreateRequest(username, email, "Password1!");
    MockMultipartFile part = new MockMultipartFile(
        "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );
    MvcResult result = mockMvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .multipart("/api/users").file(part).contentType(MediaType.MULTIPART_FORM_DATA))
        .andReturn();
    return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
  }

  private String createPublicChannelAndGetId(String name) throws Exception {
    PublicChannelCreateRequest request = new PublicChannelCreateRequest(name, "description");
    MvcResult result = mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andReturn();
    return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
  }

  @Test
  @DisplayName("PUBLIC 채널 생성 통합 테스트 - 성공")
  void createPublicChannel_success() throws Exception {
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("general", "General channel");

    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("general"))
        .andExpect(jsonPath("$.type").value("PUBLIC"));
  }

  @Test
  @DisplayName("채널 수정 통합 테스트 - 성공")
  void updateChannel_success() throws Exception {
    String channelId = createPublicChannelAndGetId("old-name");

    PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest("new-name", "New desc");

    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("new-name"));
  }

  @Test
  @DisplayName("채널 삭제 통합 테스트 - 성공")
  void deleteChannel_success() throws Exception {
    String channelId = createPublicChannelAndGetId("to-delete");

    mockMvc.perform(delete("/api/channels/{channelId}", channelId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("존재하지 않는 채널 삭제 - 404 반환")
  void deleteChannel_fail_notFound() throws Exception {
    mockMvc.perform(delete("/api/channels/{channelId}", java.util.UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("CHANNEL_NOT_FOUND"));
  }

  @Test
  @DisplayName("사용자의 채널 목록 조회 통합 테스트")
  void findChannelsByUser_success() throws Exception {
    String userId = createUserAndGetId("chanuser", "chanuser@test.com");
    createPublicChannelAndGetId("public-channel");

    mockMvc.perform(get("/api/channels")
            .param("userId", userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }
}
