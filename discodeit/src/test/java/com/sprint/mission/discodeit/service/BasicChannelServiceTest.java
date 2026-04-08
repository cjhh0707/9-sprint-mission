package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.ArgumentMatchers.any;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
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
class BasicChannelServiceTest {

  @InjectMocks
  private BasicChannelService channelService;

  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private ReadStatusRepository readStatusRepository;
  @Mock
  private MessageRepository messageRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private ChannelMapper channelMapper;

  private ChannelDto createPublicChannelDto(UUID id, String name) {
    return new ChannelDto(id, ChannelType.PUBLIC, name, "desc", List.of(), Instant.now());
  }

  private ChannelDto createPrivateChannelDto(UUID id) {
    return new ChannelDto(id, ChannelType.PRIVATE, null, null, List.of(), Instant.now());
  }

  // ======================== create PUBLIC 테스트 ========================

  @Test
  @DisplayName("PUBLIC 채널 생성 성공")
  void create_publicChannel_success() {
    // given
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("general", "General channel");
    UUID channelId = UUID.randomUUID();
    ChannelDto expectedDto = createPublicChannelDto(channelId, "general");

    given(channelRepository.save(any(Channel.class))).willReturn(new Channel(ChannelType.PUBLIC, "general", "General channel"));
    given(channelMapper.toDto(any(Channel.class))).willReturn(expectedDto);

    // when
    ChannelDto result = channelService.create(request);

    // then
    assertThat(result).isNotNull();
    assertThat(result.type()).isEqualTo(ChannelType.PUBLIC);
    assertThat(result.name()).isEqualTo("general");
  }

  @Test
  @DisplayName("PRIVATE 채널 생성 성공")
  void create_privateChannel_success() {
    // given
    UUID userId1 = UUID.randomUUID();
    UUID userId2 = UUID.randomUUID();
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of(userId1, userId2));
    UUID channelId = UUID.randomUUID();
    ChannelDto expectedDto = createPrivateChannelDto(channelId);

    given(channelRepository.save(any(Channel.class))).willReturn(new Channel(ChannelType.PRIVATE, null, null));
    given(userRepository.findAllById(List.of(userId1, userId2))).willReturn(List.of());
    given(channelMapper.toDto(any(Channel.class))).willReturn(expectedDto);

    // when
    ChannelDto result = channelService.create(request);

    // then
    assertThat(result).isNotNull();
    assertThat(result.type()).isEqualTo(ChannelType.PRIVATE);
  }

  // ======================== update 테스트 ========================

  @Test
  @DisplayName("PUBLIC 채널 수정 성공")
  void update_success() {
    // given
    UUID channelId = UUID.randomUUID();
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("new-name", "New description");
    Channel channel = new Channel(ChannelType.PUBLIC, "old-name", "Old desc");
    ChannelDto expectedDto = createPublicChannelDto(channelId, "new-name");

    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(channelMapper.toDto(any(Channel.class))).willReturn(expectedDto);

    // when
    ChannelDto result = channelService.update(channelId, request);

    // then
    assertThat(result).isNotNull();
    assertThat(result.name()).isEqualTo("new-name");
  }

  @Test
  @DisplayName("PRIVATE 채널 수정 시도 실패")
  void update_fail_privateChannel() {
    // given
    UUID channelId = UUID.randomUUID();
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("new-name", "New description");
    Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);

    given(channelRepository.findById(channelId)).willReturn(Optional.of(privateChannel));

    // when & then
    assertThatThrownBy(() -> channelService.update(channelId, request))
        .isInstanceOf(PrivateChannelUpdateException.class);
  }

  @Test
  @DisplayName("존재하지 않는 채널 수정 실패")
  void update_fail_channelNotFound() {
    // given
    UUID channelId = UUID.randomUUID();
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("new-name", "New description");
    given(channelRepository.findById(channelId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> channelService.update(channelId, request))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  // ======================== delete 테스트 ========================

  @Test
  @DisplayName("채널 삭제 성공")
  void delete_success() {
    // given
    UUID channelId = UUID.randomUUID();
    given(channelRepository.existsById(channelId)).willReturn(true);

    // when
    channelService.delete(channelId);

    // then
    then(channelRepository).should().deleteById(channelId);
  }

  @Test
  @DisplayName("존재하지 않는 채널 삭제 실패")
  void delete_fail_channelNotFound() {
    // given
    UUID channelId = UUID.randomUUID();
    given(channelRepository.existsById(channelId)).willReturn(false);

    // when & then
    assertThatThrownBy(() -> channelService.delete(channelId))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  // ======================== findByUserId 테스트 ========================

  @Test
  @DisplayName("사용자 채널 목록 조회 성공 - 채널 없음")
  void findAllByUserId_empty() {
    // given
    UUID userId = UUID.randomUUID();
    given(readStatusRepository.findAllByUserId(userId)).willReturn(List.of());
    given(channelRepository.findAllByTypeOrIdIn(any(), any())).willReturn(List.of());

    // when
    List<ChannelDto> result = channelService.findAllByUserId(userId);

    // then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("사용자 채널 목록 조회 성공 - PUBLIC 채널 포함")
  void findAllByUserId_withPublicChannels() {
    // given
    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    Channel publicChannel = new Channel(ChannelType.PUBLIC, "general", "General");
    ChannelDto expectedDto = createPublicChannelDto(channelId, "general");

    given(readStatusRepository.findAllByUserId(userId)).willReturn(List.of());
    given(channelRepository.findAllByTypeOrIdIn(any(), any())).willReturn(List.of(publicChannel));
    given(channelMapper.toDto(any(Channel.class))).willReturn(expectedDto);

    // when
    List<ChannelDto> result = channelService.findAllByUserId(userId);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).type()).isEqualTo(ChannelType.PUBLIC);
  }
}
