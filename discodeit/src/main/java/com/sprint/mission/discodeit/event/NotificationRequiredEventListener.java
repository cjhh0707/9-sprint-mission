package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Profile("disabled")
@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

  private final ReadStatusRepository readStatusRepository;
  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;
  private final CacheManager cacheManager;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void on(MessageCreatedEvent event) {
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
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void on(RoleUpdatedEvent event) {
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
  }
}