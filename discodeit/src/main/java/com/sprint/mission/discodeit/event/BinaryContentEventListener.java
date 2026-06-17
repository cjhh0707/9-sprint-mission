package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentEventListener {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentRepository binaryContentRepository;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handle(BinaryContentCreatedEvent event) {
    log.debug("바이너리 컨텐츠 저장 이벤트 수신: id={}", event.binaryContentId());
    BinaryContent binaryContent = binaryContentRepository.findById(event.binaryContentId())
        .orElseThrow(() -> new IllegalStateException(
            "BinaryContent not found: " + event.binaryContentId()));
    try {
      binaryContentStorage.put(event.binaryContentId(), event.bytes());
      binaryContent.success();
      log.info("바이너리 컨텐츠 저장 성공: id={}", event.binaryContentId());
    } catch (Exception e) {
      binaryContent.fail();
      log.error("바이너리 컨텐츠 저장 실패: id={}", event.binaryContentId(), e);
    }
  }
}