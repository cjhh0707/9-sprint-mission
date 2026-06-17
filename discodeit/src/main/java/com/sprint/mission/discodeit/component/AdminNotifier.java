package com.sprint.mission.discodeit.component;

import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminNotifier {

  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void notifyAdmin(String title, String content) {
    List<User> admins = userRepository.findAllByRole(Role.ADMIN);
    if (admins.isEmpty()) {
      log.warn("관리자 계정이 없어 알림을 전송하지 못했습니다.");
      return;
    }
    List<Notification> notifications = admins.stream()
        .map(admin -> new Notification(admin, title, content))
        .toList();
    notificationRepository.saveAll(notifications);
    log.info("관리자 알림 전송 완료: title={}", title);
  }
}