package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  // cursor가 없을 때 (첫 페이지)
  List<Message> findAllByChannelIdOrderByCreatedAtDesc(UUID channelId, Pageable pageable);

  // cursor가 있을 때 (cursor 이전 메시지)
  List<Message> findAllByChannelIdAndCreatedAtBeforeOrderByCreatedAtDesc(UUID channelId,
      Instant cursor, Pageable pageable);

  void deleteAllByChannelId(UUID channelId);
}