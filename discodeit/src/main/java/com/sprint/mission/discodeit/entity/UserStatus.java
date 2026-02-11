package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends BaseEntity {
    private final UUID userId;
    private Instant lastLogin;

    public UserStatus(UUID userId) {
        super();
        this.userId = userId;
        this.lastLogin = Instant.now();
    }

    //마지막 접속시간 업데이트
    public void updateLastLogin(Instant lastLogin) {
        this.lastLogin = lastLogin;
        setUpdatedAt();
    }

    public String isLoggedIn() {
        Instant now = Instant.now();
        long diff = (now.toEpochMilli() - lastLogin.toEpochMilli()) / (1000 * 60);
        if (diff < 5) {
            return "접속 중";
        } else {
            return "접속중 아님";
        }
    }
}
