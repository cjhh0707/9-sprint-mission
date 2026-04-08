package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;
  @Autowired
  private ChannelRepository channelRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private UserStatusRepository userStatusRepository;

  private Channel channel;
  private User author;

  @BeforeEach
  void setUp() {
    channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "General"));
    author = userRepository.save(new User("author", "author@test.com", "pass", null));
    userStatusRepository.save(new UserStatus(author, Instant.now()));
  }

  @Test
  @DisplayName("채널별 메시지 커서 기반 페이징 조회 성공")
  void findAllByChannelIdWithAuthor_success() {
    messageRepository.save(new Message("msg1", channel, author, List.of()));
    messageRepository.save(new Message("msg2", channel, author, List.of()));

    PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
    Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(
        channel.getId(), Instant.now().plusSeconds(1), pageable);

    assertThat(result.getContent()).hasSize(2);
  }

  @Test
  @DisplayName("커서 이전 메시지가 없을 때 빈 결과 반환")
  void findAllByChannelIdWithAuthor_empty() {
    messageRepository.save(new Message("msg1", channel, author, List.of()));

    PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
    Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(
        channel.getId(), Instant.now().minusSeconds(60), pageable);

    assertThat(result.getContent()).isEmpty();
  }

  @Test
  @DisplayName("채널의 마지막 메시지 시간 조회 성공")
  void findLastMessageAtByChannelId_success() {
    messageRepository.save(new Message("first", channel, author, List.of()));
    messageRepository.save(new Message("last", channel, author, List.of()));

    Optional<Instant> result = messageRepository.findLastMessageAtByChannelId(channel.getId());

    assertThat(result).isPresent();
  }

  @Test
  @DisplayName("메시지가 없을 때 마지막 메시지 시간 조회 결과 없음")
  void findLastMessageAtByChannelId_noMessages() {
    Optional<Instant> result = messageRepository.findLastMessageAtByChannelId(channel.getId());

    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("채널 ID로 메시지 전체 삭제")
  void deleteAllByChannelId_success() {
    messageRepository.save(new Message("msg1", channel, author, List.of()));
    messageRepository.save(new Message("msg2", channel, author, List.of()));

    messageRepository.deleteAllByChannelId(channel.getId());

    PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
    Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(
        channel.getId(), Instant.now().plusSeconds(1), pageable);

    assertThat(result.getContent()).isEmpty();
  }
}
