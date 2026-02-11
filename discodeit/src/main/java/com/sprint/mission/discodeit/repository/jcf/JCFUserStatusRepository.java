package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "jcf",
        matchIfMissing = true
)
public class JCFUserStatusRepository implements UserStatusRepository {
    private final Map<UUID, UserStatus> data = new HashMap<>();

    @Override
    public UserStatus save(UserStatus userStatus) {
        data.put(userStatus.getId(), userStatus);
        return userStatus;
    }

    @Override
    public UserStatus findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }

    @Override
    public UserStatus findByUserId(UUID userId) {
        return data.values().stream()
                .filter(userStatus -> userStatus.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        UserStatus userStatus = findByUserId(userId);
        if (userStatus != null) {
            data.remove(userStatus.getId());
        }
    }
}
