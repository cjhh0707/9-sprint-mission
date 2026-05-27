package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.config.TestSecurityConfig;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
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
@Import(TestSecurityConfig.class)
class MessageIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private UUID channelId;
  private UUID authorId;

  @BeforeEach
  void setUp() throws Exception {
    UserCreateRequest userReq = new UserCreateRequest("msgauthor", "msgauthor@test.com", "Password1!");
    MockMultipartFile userPart = new MockMultipartFile(
        "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(userReq)
    );
    MvcResult userResult = mockMvc.perform(multipart("/api/users")
            .file(userPart).contentType(MediaType.MULTIPART_FORM_DATA))
        .andReturn();
    authorId = UUID.fromString(
        objectMapper.readTree(userResult.getResponse().getContentAsString()).get("id").asText());

    PublicChannelCreateRequest channelReq = new PublicChannelCreateRequest("msg-channel", "desc");
    MvcResult channelResult = mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(channelReq)))
        .andReturn();
    channelId = UUID.fromString(
        objectMapper.readTree(channelResult.getResponse().getContentAsString()).get("id").asText());
  }

  private String createMessageAndGetId(String content) throws Exception {
    MessageCreateRequest request = new MessageCreateRequest(content, channelId, authorId);
    MockMultipartFile part = new MockMultipartFile(
        "messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );
    MvcResult result = mockMvc.perform(multipart("/api/messages")
            .file(part).contentType(MediaType.MULTIPART_FORM_DATA))
        .andReturn();
    return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
  }

  @Test
  @DisplayName("메시지 생성 통합 테스트 - 성공")
  void createMessage_success() throws Exception {
    MessageCreateRequest request = new MessageCreateRequest("Hello world!", channelId, authorId);
    MockMultipartFile part = new MockMultipartFile(
        "messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    mockMvc.perform(multipart("/api/messages")
            .file(part).contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.content").value("Hello world!"))
        .andExpect(jsonPath("$.id").isNotEmpty());
  }

  @Test
  @DisplayName("메시지 수정 통합 테스트 - 성공")
  void updateMessage_success() throws Exception {
    String messageId = createMessageAndGetId("Original content");

    MessageUpdateRequest updateRequest = new MessageUpdateRequest("Updated content");

    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").value("Updated content"));
  }

  @Test
  @DisplayName("메시지 삭제 통합 테스트 - 성공")
  void deleteMessage_success() throws Exception {
    String messageId = createMessageAndGetId("To be deleted");

    mockMvc.perform(delete("/api/messages/{messageId}", messageId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("채널별 메시지 목록 조회 통합 테스트")
  void findMessagesByChannel_success() throws Exception {
    createMessageAndGetId("msg1");
    createMessageAndGetId("msg2");

    mockMvc.perform(get("/api/messages")
            .param("channelId", channelId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray());
  }

  @Test
  @DisplayName("존재하지 않는 메시지 삭제 - 404 반환")
  void deleteMessage_fail_notFound() throws Exception {
    mockMvc.perform(delete("/api/messages/{messageId}", UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("MESSAGE_NOT_FOUND"));
  }
}
