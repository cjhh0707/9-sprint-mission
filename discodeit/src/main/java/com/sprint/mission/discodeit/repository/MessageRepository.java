package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);
    Message findById(UUID id);
    List<Message> findAll();
    List<Message> findByAuthorId(UUID authorId);
    List<Message> findByChannelId(UUID channelId);
    void delete(UUID id);
    int deleteByAuthorId(UUID authorId);
    int deleteByChannelId(UUID channelId);

    Instant findLastMessageTimeByChannelId(UUID channelId);
}
