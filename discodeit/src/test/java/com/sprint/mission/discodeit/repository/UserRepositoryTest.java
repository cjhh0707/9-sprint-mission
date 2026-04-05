package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserStatusRepository userStatusRepository;

  private User createAndSaveUser(String username, String email) {
    User user = new User(username, email, "password123", null);
    userRepository.save(user);
    UserStatus status = new UserStatus(user, Instant.now());
    userStatusRepository.save(status);
    return user;
  }

  @Test
  @DisplayName("이메일로 존재 여부 확인 - 존재하는 경우")
  void existsByEmail_exists() {
    createAndSaveUser("user1", "user1@test.com");

    boolean exists = userRepository.existsByEmail("user1@test.com");

    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("이메일로 존재 여부 확인 - 존재하지 않는 경우")
  void existsByEmail_notExists() {
    boolean exists = userRepository.existsByEmail("notexist@test.com");

    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("사용자명으로 사용자 조회 성공")
  void findByUsername_success() {
    createAndSaveUser("findme", "findme@test.com");

    Optional<User> result = userRepository.findByUsername("findme");

    assertThat(result).isPresent();
    assertThat(result.get().getUsername()).isEqualTo("findme");
  }

  @Test
  @DisplayName("사용자명으로 사용자 조회 실패 - 없는 사용자")
  void findByUsername_notFound() {
    Optional<User> result = userRepository.findByUsername("nobody");

    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("프로파일과 상태를 포함한 전체 사용자 조회")
  void findAllWithProfileAndStatus_success() {
    createAndSaveUser("alpha", "alpha@test.com");
    createAndSaveUser("beta", "beta@test.com");

    List<User> users = userRepository.findAllWithProfileAndStatus();

    assertThat(users).hasSize(2);
  }

  @Test
  @DisplayName("사용자명 중복 여부 확인")
  void existsByUsername_exists() {
    createAndSaveUser("dupuser", "dup@test.com");

    boolean exists = userRepository.existsByUsername("dupuser");

    assertThat(exists).isTrue();
  }
}
