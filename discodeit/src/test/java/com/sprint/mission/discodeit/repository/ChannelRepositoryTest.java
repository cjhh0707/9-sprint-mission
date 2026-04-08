package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
class ChannelRepositoryTest {

  @Autowired
  private ChannelRepository channelRepository;

  @Test
  @DisplayName("PUBLIC 타입 또는 특정 ID 포함 채널 목록 조회")
  void findAllByTypeOrIdIn_success() {
    Channel publicChannel = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "General"));
    Channel privateChannel = channelRepository.save(new Channel(ChannelType.PRIVATE, null, null));

    List<Channel> result = channelRepository.findAllByTypeOrIdIn(
        ChannelType.PUBLIC,
        List.of(privateChannel.getId())
    );

    assertThat(result).hasSize(2);
    assertThat(result).extracting(Channel::getId)
        .containsExactlyInAnyOrder(publicChannel.getId(), privateChannel.getId());
  }

  @Test
  @DisplayName("일치하는 채널이 없을 때 빈 목록 반환")
  void findAllByTypeOrIdIn_empty() {
    channelRepository.save(new Channel(ChannelType.PRIVATE, null, null));

    List<Channel> result = channelRepository.findAllByTypeOrIdIn(
        ChannelType.PUBLIC,
        List.of(UUID.randomUUID())
    );

    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("채널 저장 및 조회 성공")
  void save_and_findById_success() {
    Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "test", "Test channel"));

    assertThat(channelRepository.findById(channel.getId())).isPresent();
  }

  @Test
  @DisplayName("채널 삭제 후 조회 실패")
  void delete_success() {
    Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "to-delete", "delete me"));
    channelRepository.deleteById(channel.getId());

    assertThat(channelRepository.findById(channel.getId())).isEmpty();
  }
}
