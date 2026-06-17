package com.sprint.mission.discodeit.config;

import java.util.Map;
import java.util.concurrent.Executor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@EnableRetry
@EnableAsync
@Configuration
public class AsyncConfig {

  @Bean
  public ThreadPoolTaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(25);
    executor.setThreadNamePrefix("async-");
    executor.setTaskDecorator(taskDecorator());
    executor.initialize();
    return executor;
  }

  @Bean
  public TaskDecorator taskDecorator() {
    return runnable -> {
      // 현재 스레드에서 MDC와 SecurityContext 캡처
      Map<String, String> mdcContext = MDC.getCopyOfContextMap();
      SecurityContext securityContext = SecurityContextHolder.getContext();

      return () -> {
        try {
          // 비동기 스레드에 MDC 복원
          if (mdcContext != null) {
            MDC.setContextMap(mdcContext);
          }
          // 비동기 스레드에 SecurityContext 복원
          SecurityContextHolder.setContext(securityContext);
          runnable.run();
        } finally {
          MDC.clear();
          SecurityContextHolder.clearContext();
        }
      };
    };
  }

  @Bean("eventTaskExecutor")
  public Executor eventTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(25);
    executor.setThreadNamePrefix("event-async-");
    executor.initialize();
    return executor;
  }
}