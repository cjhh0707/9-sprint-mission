package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.message.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.message.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.message.S3UploadFailedEvent;
import com.sprint.mission.discodeit.event.message.UserLogInOutEvent;
import com.sprint.mission.discodeit.event.sse.BinaryContentStatusUpdatedEvent;
import com.sprint.mission.discodeit.event.sse.ChannelEvent;
import com.sprint.mission.discodeit.event.sse.NotificationCreatedEvent;
import com.sprint.mission.discodeit.event.sse.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 분산 환경에서 로컬 ApplicationEvent만으로는 메시지를 생성/발생시킨 인스턴스에서만
 * 처리가 끝나버린다. 웹소켓 구독자나 SSE 연결을 보유한 인스턴스가 다를 수 있으므로,
 * 실시간 푸시가 필요한 이벤트를 Kafka로 발행해 모든 인스턴스에 전파한다.
 * (수신 측은 event.kafka.WebSocketRequiredEventListener, SseRequiredTopicListener 참고)
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProduceRequiredEventListener {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  @Async("eventTaskExecutor")
  @TransactionalEventListener
  public void on(MessageCreatedEvent event) {
    sendToKafka(event);
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener
  public void on(RoleUpdatedEvent event) {
    sendToKafka(event);
  }

  @Async("eventTaskExecutor")
  @EventListener
  public void on(S3UploadFailedEvent event) {
    sendToKafka(event);
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener
  public void on(BinaryContentStatusUpdatedEvent event) {
    sendToKafka(event);
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener
  public void on(NotificationCreatedEvent event) {
    sendToKafka(event);
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener
  public void on(ChannelEvent event) {
    sendToKafka(event);
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener
  public void on(UserEvent event) {
    sendToKafka(event);
  }

  @Async("eventTaskExecutor")
  @EventListener
  public void on(UserLogInOutEvent event) {
    sendToKafka(event);
  }

  private <T> void sendToKafka(T event) {
    try {
      String payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send("discodeit.".concat(event.getClass().getSimpleName()), payload);
    } catch (JsonProcessingException e) {
      log.error("Failed to send event to Kafka", e);
      throw new RuntimeException(e);
    }
  }
}
