package com.sprint.mission.discodeit.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChannelController.class)
class ChannelControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private ChannelService channelService;

  private ChannelDto createPublicChannelDto(UUID id, String name) {
    return new ChannelDto(id, ChannelType.PUBLIC, name, "desc", List.of(), Instant.now());
  }

  @Test
  @DisplayName("PUBLIC 채널 생성 성공 - 201 반환")
  void createPublicChannel_success() throws Exception {
    UUID channelId = UUID.randomUUID();
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("general", "General channel");
    ChannelDto response = createPublicChannelDto(channelId, "general");

    given(channelService.create(request)).willReturn(response);

    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(channelId.toString()))
        .andExpect(jsonPath("$.name").value("general"))
        .andExpect(jsonPath("$.type").value("PUBLIC"));
  }

  @Test
  @DisplayName("채널 이름 누락으로 PUBLIC 채널 생성 실패 - 400 반환")
  void createPublicChannel_fail_validation() throws Exception {
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("", "desc");

    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("채널 목록 조회 성공 - 200 반환")
  void findAll_success() throws Exception {
    UUID userId = UUID.randomUUID();
    List<ChannelDto> channels = List.of(
        createPublicChannelDto(UUID.randomUUID(), "general"),
        createPublicChannelDto(UUID.randomUUID(), "random")
    );

    given(channelService.findAllByUserId(userId)).willReturn(channels);

    mockMvc.perform(get("/api/channels")
            .param("userId", userId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  @DisplayName("채널 삭제 성공 - 204 반환")
  void delete_success() throws Exception {
    UUID channelId = UUID.randomUUID();
    willDoNothing().given(channelService).delete(channelId);

    mockMvc.perform(delete("/api/channels/{channelId}", channelId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("존재하지 않는 채널 삭제 - 404 반환")
  void delete_fail_notFound() throws Exception {
    UUID channelId = UUID.randomUUID();
    willThrow(new ChannelNotFoundException(channelId)).given(channelService).delete(channelId);

    mockMvc.perform(delete("/api/channels/{channelId}", channelId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("CHANNEL_NOT_FOUND"));
  }
}
