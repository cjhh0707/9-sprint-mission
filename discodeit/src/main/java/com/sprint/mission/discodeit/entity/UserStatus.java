package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user_statuses")
public class UserStatus extends BaseUpdatableEntity {

  @JsonBackReference
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", columnDefinition = "uuid", nullable = false, unique = true)
  private User user;

  @Column(columnDefinition = "timestamp with time zone", nullable = false)
  private Instant lastActiveAt;

  public UserStatus(User user, Instant lastActiveAt) {
    this.user = user;
    this.lastActiveAt = lastActiveAt;
    user.setStatus(this);
  }

  public void update(Instant lastActiveAt) {
    this.lastActiveAt = lastActiveAt;
  }

  public boolean isOnline() {
    return lastActiveAt.isAfter(Instant.now().minusSeconds(300));
  }
}