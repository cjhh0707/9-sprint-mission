package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.component.AdminNotifier;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {

  private final ObjectMapper objectMapper;
  private final ReadStatusRepository readStatusRepository;
  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;
  private final CacheManager cacheManager;
  private final AdminNotifier adminNotifier;

  @KafkaListener(topics = "discodeit.MessageCreatedEvent")
  @Transactional
  public void onMessageCreatedEvent(String kafkaEvent) {
    try {
      MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);

      List<ReadStatus> readStatuses = readStatusRepository
          .findAllByChannelIdWithUser(event.channelId())
          .stream()
          .filter(ReadStatus::isNotificationEnabled)
          .filter(rs -> !rs.getUser().getId().equals(event.authorId()))
          .toList();

      List<Notification> notifications = readStatuses.stream()
          .map(rs -> new Notification(
              rs.getUser(),
              event.authorUsername() + " (#" + event.channelName() + ")",
              event.content()
          ))
          .toList();

      notificationRepository.saveAll(notifications);
      log.info("메시지 알림 생성 완료: channelId={}, 알림 수={}", event.channelId(), notifications.size());
      cacheManager.getCache("notifications").clear();
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
  @Transactional
  public void onRoleUpdatedEvent(String kafkaEvent) {
    try {
      RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);

      User user = userRepository.findById(event.userId())
          .orElseThrow(() -> UserNotFoundException.withId(event.userId()));

      Notification notification = new Notification(
          user,
          "권한이 변경되었습니다.",
          event.previousRole().name() + " -> " + event.newRole().name()
      );
      notificationRepository.save(notification);
      log.info("권한 변경 알림 생성 완료: userId={}", event.userId());
      cacheManager.getCache("notifications").clear();
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @KafkaListener(topics = "discodeit.S3UploadFailedEvent")
  @Transactional
  public void onS3UploadFailedEvent(String kafkaEvent) {
    try {
      S3UploadFailedEvent event = objectMapper.readValue(kafkaEvent, S3UploadFailedEvent.class);
      adminNotifier.notifyAdmin(
          "S3 업로드 실패",
          "binaryContentId: " + event.binaryContentId()
      );
      log.info("S3 업로드 실패 알림 생성 완료: binaryContentId={}", event.binaryContentId());
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}