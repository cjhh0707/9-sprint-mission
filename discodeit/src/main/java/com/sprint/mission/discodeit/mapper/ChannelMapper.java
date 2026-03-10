package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public abstract class ChannelMapper {

  @Autowired
  protected MessageRepository messageRepository;
  @Autowired
  protected ReadStatusRepository readStatusRepository;
  @Autowired
  protected UserMapper userMapper;

  @Mapping(target = "lastMessageAt", expression = "java(getLastMessageAt(channel))")
  @Mapping(target = "participants", expression = "java(getParticipants(channel))")
  public abstract ChannelDto toDto(Channel channel);

  protected Instant getLastMessageAt(Channel channel) {
    return messageRepository
        .findAllByChannelIdOrderByCreatedAtDesc(channel.getId(), PageRequest.of(0, 1))
        .stream()
        .map(Message::getCreatedAt)
        .findFirst()
        .orElse(null);
  }

  protected List<UserDto> getParticipants(Channel channel) {
    if (channel.getType() == ChannelType.PRIVATE) {
      return readStatusRepository.findAllByChannelId(channel.getId())
          .stream()
          .map(rs -> userMapper.toDto(rs.getUser()))
          .toList();
    }
    return null;
  }

}