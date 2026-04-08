package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.ArgumentMatchers.any;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;

@ExtendWith(MockitoExtension.class)
class BasicMessageServiceTest {

  @InjectMocks
  private BasicMessageService messageService;

  @Mock
  private MessageRepository messageRepository;
  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private MessageMapper messageMapper;
  @Mock
  private BinaryContentStorage binaryContentStorage;
  @Mock
  private BinaryContentRepository binaryContentRepository;
  @Mock
  private PageResponseMapper pageResponseMapper;

  private MessageDto createMockMessageDto(UUID id, String content) {
    return new MessageDto(id, Instant.now(), Instant.now(), content, UUID.randomUUID(), null, List.of());
  }

  // ======================== create 테스트 ========================

  @Test
  @DisplayName("메시지 생성 성공")
  void create_success() {
    // given
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("Hello!", channelId, authorId);
    Channel channel = new Channel(ChannelType.PUBLIC, "general", "desc");
    User author = new User("author", "author@test.com", "pass", null);
    UUID messageId = UUID.randomUUID();
    MessageDto expectedDto = createMockMessageDto(messageId, "Hello!");

    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(userRepository.findById(authorId)).willReturn(Optional.of(author));
    given(messageRepository.save(any(Message.class))).willReturn(new Message("Hello!", channel, author, List.of()));
    given(messageMapper.toDto(any(Message.class))).willReturn(expectedDto);

    // when
    MessageDto result = messageService.create(request, List.of());

    // then
    assertThat(result).isNotNull();
    assertThat(result.content()).isEqualTo("Hello!");
    then(messageRepository).should().save(any(Message.class));
  }

  @Test
  @DisplayName("존재하지 않는 채널에 메시지 생성 실패")
  void create_fail_channelNotFound() {
    // given
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("Hello!", channelId, authorId);
    given(channelRepository.findById(channelId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> messageService.create(request, List.of()))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  @Test
  @DisplayName("존재하지 않는 사용자로 메시지 생성 실패")
  void create_fail_authorNotFound() {
    // given
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("Hello!", channelId, authorId);
    Channel channel = new Channel(ChannelType.PUBLIC, "general", "desc");

    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(userRepository.findById(authorId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> messageService.create(request, List.of()))
        .isInstanceOf(UserNotFoundException.class);
  }

  // ======================== update 테스트 ========================

  @Test
  @DisplayName("메시지 수정 성공")
  void update_success() {
    // given
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("Updated content");
    Channel channel = new Channel(ChannelType.PUBLIC, "general", "desc");
    User author = new User("author", "author@test.com", "pass", null);
    Message message = new Message("Original content", channel, author, List.of());
    MessageDto expectedDto = createMockMessageDto(messageId, "Updated content");

    given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
    given(messageMapper.toDto(any(Message.class))).willReturn(expectedDto);

    // when
    MessageDto result = messageService.update(messageId, request);

    // then
    assertThat(result).isNotNull();
    assertThat(result.content()).isEqualTo("Updated content");
  }

  @Test
  @DisplayName("존재하지 않는 메시지 수정 실패")
  void update_fail_messageNotFound() {
    // given
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("Updated content");
    given(messageRepository.findById(messageId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> messageService.update(messageId, request))
        .isInstanceOf(MessageNotFoundException.class);
  }

  // ======================== delete 테스트 ========================

  @Test
  @DisplayName("메시지 삭제 성공")
  void delete_success() {
    // given
    UUID messageId = UUID.randomUUID();
    given(messageRepository.existsById(messageId)).willReturn(true);

    // when
    messageService.delete(messageId);

    // then
    then(messageRepository).should().deleteById(messageId);
  }

  @Test
  @DisplayName("존재하지 않는 메시지 삭제 실패")
  void delete_fail_messageNotFound() {
    // given
    UUID messageId = UUID.randomUUID();
    given(messageRepository.existsById(messageId)).willReturn(false);

    // when & then
    assertThatThrownBy(() -> messageService.delete(messageId))
        .isInstanceOf(MessageNotFoundException.class);
  }

  // ======================== findByChannelId 테스트 ========================

  @Test
  @DisplayName("채널별 메시지 목록 조회 성공")
  void findAllByChannelId_success() {
    // given
    UUID channelId = UUID.randomUUID();
    PageRequest pageable = PageRequest.of(0, 10);
    SliceImpl<Message> slice = new SliceImpl<>(List.of(), pageable, false);
    PageResponse<MessageDto> expectedResponse = new PageResponse<>(List.of(), null, 0, false, 0L);

    given(messageRepository.findAllByChannelIdWithAuthor(any(), any(), any())).willReturn(slice);
    given(pageResponseMapper.fromSlice(any(), any())).willAnswer(inv -> expectedResponse);

    // when
    PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, null, pageable);

    // then
    assertThat(result).isNotNull();
    assertThat(result.content()).isEmpty();
  }
}
