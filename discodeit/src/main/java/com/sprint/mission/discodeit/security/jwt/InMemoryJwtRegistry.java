package com.sprint.mission.discodeit.security.jwt;

import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InMemoryJwtRegistry implements JwtRegistry {

  // <userId, Queue<JwtInformation>>
  private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
  private final int maxActiveJwtCount;

  public InMemoryJwtRegistry(
      @Value("${discodeit.jwt.max-active-count:1}") int maxActiveJwtCount
  ) {
    this.maxActiveJwtCount = maxActiveJwtCount;
  }

  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
    UUID userId = jwtInformation.userId();
    Queue<JwtInformation> queue = origin.computeIfAbsent(userId, id -> new ConcurrentLinkedQueue<>());

    // 최대 동시 로그인 수 초과 시 가장 오래된 토큰 제거 (기존 세션 무효화)
    while (queue.size() >= maxActiveJwtCount) {
      JwtInformation evicted = queue.poll();
      log.debug("기존 JWT 무효화 (최대 동시 로그인 초과): userId={}", userId);
    }

    queue.add(jwtInformation);
    log.debug("JWT 등록: userId={}", userId);
  }

  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
    origin.remove(userId);
    log.debug("JWT 무효화 완료: userId={}", userId);
  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    Queue<JwtInformation> queue = origin.get(userId);
    if (queue == null) {
      return false;
    }
    return queue.stream().anyMatch(info -> !info.isExpired());
  }

  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
    return origin.values().stream()
        .flatMap(Collection::stream)
        .anyMatch(info -> !info.isExpired() && info.accessToken().equals(accessToken));
  }

  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
    return origin.values().stream()
        .flatMap(Collection::stream)
        .anyMatch(info -> !info.isExpired() && info.refreshToken().equals(refreshToken));
  }

  @Override
  public void rotateJwtInformation(String oldRefreshToken, JwtInformation newJwtInformation) {
    UUID userId = newJwtInformation.userId();
    Queue<JwtInformation> queue = origin.get(userId);
    if (queue != null) {
      queue.removeIf(info -> info.refreshToken().equals(oldRefreshToken));
      queue.add(newJwtInformation);
      log.debug("JWT 로테이션 완료: userId={}", userId);
    }
  }

  @Scheduled(fixedDelay = 1000 * 60 * 5)
  @Override
  public void clearExpiredJwtInformation() {
    origin.forEach((userId, queue) -> queue.removeIf(JwtInformation::isExpired));
    origin.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    log.debug("만료된 JWT 정보 삭제 완료");
  }
}