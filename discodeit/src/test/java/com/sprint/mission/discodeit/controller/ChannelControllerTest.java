package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.ArrayList;
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

  @Test
  @DisplayName("공개 채널 생성 성공 테스트")
  void createPublicChannel_success() throws Exception {
    // Given
    String channelName = "test-channel";
    String channelDescription = "채널 설명입니다.";
    UUID channelId = UUID.randomUUID();

    PublicChannelCreateRequest createRequest = new PublicChannelCreateRequest(
        channelName,
        channelDescription
    );
    ChannelDto createdChannel = new ChannelDto(
        channelId,
        ChannelType.PUBLIC,
        channelName,
        channelDescription,
        new ArrayList<>(),
        Instant.now()
    );

    given(channelService.create(any(PublicChannelCreateRequest.class)))
        .willReturn(createdChannel);

    // When & Then
    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(channelId.toString()))
        .andExpect(jsonPath("$.type").value("PUBLIC"))
        .andExpect(jsonPath("$.name").value(channelName))
        .andExpect(jsonPath("$.description").value(channelDescription));
  }

  @Test
  @DisplayName("공개 채널 생성 실패 테스트 - 유효하지 않은 요청")
  void createPublicChannel_failure_invalidRequest() throws Exception {
    // Given - 이름 최소 길이(2자) 위반, 설명 최대 길이(255자) 위반
    String tooShortName = "a";
    String tooLongDescription = "채널 설명은 최대 255자까지 가능합니다.".repeat(10);

    PublicChannelCreateRequest invalidRequest = new PublicChannelCreateRequest(
        tooShortName,
        tooLongDescription
    );

    // When & Then
    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("비공개 채널 생성 성공 테스트")
  void createPrivateChannel_success() throws Exception {
    // Given
    UUID userId1 = UUID.randomUUID();
    UUID userId2 = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();

    List<UUID> participantIds = List.of(userId1, userId2);
    PrivateChannelCreateRequest createRequest = new PrivateChannelCreateRequest(participantIds);

    List<UserDto> participants = List.of(
        new UserDto(userId1, "user-" + userId1.toString().substring(0, 5),
            "user1@example.com", null, false),
        new UserDto(userId2, "user-" + userId2.toString().substring(0, 5),
            "user2@example.com", null, false)
    );
    ChannelDto createdChannel = new ChannelDto(
        channelId,
        ChannelType.PRIVATE,
        null,
        null,
        participants,
        Instant.now()
    );

    given(channelService.create(any(PrivateChannelCreateRequest.class)))
        .willReturn(createdChannel);

    // When & Then
    mockMvc.perform(post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(channelId.toString()))
        .andExpect(jsonPath("$.type").value("PRIVATE"))
        .andExpect(jsonPath("$.participants").isArray())
        .andExpect(jsonPath("$.participants.length()").value(2));
  }

  @Test
  @DisplayName("공개 채널 업데이트 성공 테스트")
  void updateChannel_success() throws Exception {
    // Given
    UUID channelId = UUID.randomUUID();
    String updatedName = "updated-channel";
    String updatedDescription = "업데이트된 채널 설명입니다.";

    PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest(
        updatedName,
        updatedDescription
    );
    ChannelDto updatedChannel = new ChannelDto(
        channelId,
        ChannelType.PUBLIC,
        updatedName,
        updatedDescription,
        new ArrayList<>(),
        Instant.now()
    );

    given(channelService.update(eq(channelId), any(PublicChannelUpdateRequest.class)))
        .willReturn(updatedChannel);

    // When & Then
    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(channelId.toString()))
        .andExpect(jsonPath("$.name").value(updatedName))
        .andExpect(jsonPath("$.description").value(updatedDescription));
  }

  @Test
  @DisplayName("채널 업데이트 실패 테스트 - 존재하지 않는 채널")
  void updateChannel_failure_channelNotFound() throws Exception {
    // Given
    UUID nonExistentChannelId = UUID.randomUUID();
    PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest(
        "updated-channel",
        "업데이트된 채널 설명입니다."
    );

    given(channelService.update(eq(nonExistentChannelId), any(PublicChannelUpdateRequest.class)))
        .willThrow(ChannelNotFoundException.withId(nonExistentChannelId));

    // When & Then
    mockMvc.perform(patch("/api/channels/{channelId}", nonExistentChannelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("채널 업데이트 실패 테스트 - 비공개 채널 업데이트 시도")
  void updateChannel_failure_privateChannelUpdate() throws Exception {
    // Given
    UUID privateChannelId = UUID.randomUUID();
    PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest(
        "updated-channel",
        "업데이트된 채널 설명입니다."
    );

    given(channelService.update(eq(privateChannelId), any(PublicChannelUpdateRequest.class)))
        .willThrow(PrivateChannelUpdateException.forChannel(privateChannelId));

    // When & Then
    mockMvc.perform(patch("/api/channels/{channelId}", privateChannelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("채널 삭제 성공 테스트")
  void deleteChannel_success() throws Exception {
    // Given
    UUID channelId = UUID.randomUUID();
    willDoNothing().given(channelService).delete(channelId);

    // When & Then
    mockMvc.perform(delete("/api/channels/{channelId}", channelId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("채널 삭제 실패 테스트 - 존재하지 않는 채널")
  void deleteChannel_failure_channelNotFound() throws Exception {
    // Given
    UUID nonExistentChannelId = UUID.randomUUID();
    willThrow(ChannelNotFoundException.withId(nonExistentChannelId))
        .given(channelService).delete(nonExistentChannelId);

    // When & Then
    mockMvc.perform(delete("/api/channels/{channelId}", nonExistentChannelId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("사용자별 채널 목록 조회 성공 테스트")
  void findAllByUserId_success() throws Exception {
    // Given
    UUID userId = UUID.randomUUID();
    UUID channelId1 = UUID.randomUUID();
    UUID channelId2 = UUID.randomUUID();
    String publicChannelName = "public-channel";
    String publicChannelDescription = "공개 채널 설명";

    List<ChannelDto> channels = List.of(
        new ChannelDto(
            channelId1,
            ChannelType.PUBLIC,
            publicChannelName,
            publicChannelDescription,
            new ArrayList<>(),
            Instant.now()
        ),
        new ChannelDto(
            channelId2,
            ChannelType.PRIVATE,
            null,
            null,
            List.of(new UserDto(userId, "user1", "user1@example.com", null, true)),
            Instant.now().minusSeconds(3600)
        )
    );

    given(channelService.findAllByUserId(userId)).willReturn(channels);

    // When & Then
    mockMvc.perform(get("/api/channels")
            .param("userId", userId.toString())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(channelId1.toString()))
        .andExpect(jsonPath("$[0].type").value("PUBLIC"))
        .andExpect(jsonPath("$[0].name").value(publicChannelName))
        .andExpect(jsonPath("$[1].id").value(channelId2.toString()))
        .andExpect(jsonPath("$[1].type").value("PRIVATE"));
  }
}
