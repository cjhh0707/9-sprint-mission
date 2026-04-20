package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;

/**
 * Repository 슬라이스 테스트에서 공통으로 사용하는 엔티티 생성 픽스처.
 * 엔티티 빌드 로직을 중앙화하여 중복을 제거하고 일관성을 유지합니다.
 */
public class RepositoryTestFixture {

    private static final String DEFAULT_PASSWORD = "password123!@#";
    private static final String DEFAULT_PROFILE_NAME = "profile.jpg";
    private static final long DEFAULT_PROFILE_SIZE = 1024L;
    private static final String DEFAULT_PROFILE_TYPE = "image/jpeg";

    /**
     * 프로필과 상태(UserStatus)가 포함된 테스트용 User 엔티티를 생성합니다.
     * 반환된 객체는 Repository를 통해 저장해야 합니다.
     */
    public static User buildUser(String username, String email) {
        BinaryContent profile = new BinaryContent(
            DEFAULT_PROFILE_NAME,
            DEFAULT_PROFILE_SIZE,
            DEFAULT_PROFILE_TYPE
        );
        User user = new User(username, email, DEFAULT_PASSWORD, profile);
        new UserStatus(user, Instant.now());
        return user;
    }

    /**
     * 마지막 활성 시간을 지정한 테스트용 User 엔티티를 생성합니다.
     */
    public static User buildUser(String username, String email, Instant lastActiveAt) {
        BinaryContent profile = new BinaryContent(
            DEFAULT_PROFILE_NAME,
            DEFAULT_PROFILE_SIZE,
            DEFAULT_PROFILE_TYPE
        );
        User user = new User(username, email, DEFAULT_PASSWORD, profile);
        new UserStatus(user, lastActiveAt);
        return user;
    }

    /**
     * 테스트용 Channel 엔티티를 생성합니다.
     * 반환된 객체는 Repository를 통해 저장해야 합니다.
     */
    public static Channel buildChannel(ChannelType type, String name) {
        return new Channel(type, name, "설명: " + name);
    }
}
