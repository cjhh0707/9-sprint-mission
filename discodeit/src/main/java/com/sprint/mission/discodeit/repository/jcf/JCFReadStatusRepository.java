package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "jcf",
        matchIfMissing = true
)
public class JCFReadStatusRepository implements ReadStatusRepository {
    private final Map<UUID, ReadStatus> data = new HashMap<>();

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        data.put(readStatus.getId(), readStatus);
        return readStatus;
    }

    @Override
    public ReadStatus findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<ReadStatus> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }

    @Override
    public List<ReadStatus> findByChannelId(UUID channelId) {
        return data.values().stream()
                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public int deleteByChannelId(UUID channelId) {
        List<ReadStatus> readStatuses = findByChannelId(channelId);
        for (ReadStatus readStatus : readStatuses) {
            data.remove(readStatus.getId());
        }
        return readStatuses.size();
    }

    @Override
    public List<ReadStatus> findByUserId(UUID userId) {
        return data.values().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public ReadStatus findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return data.values().stream()
                .filter(readStatus ->
                        readStatus.getUserId().equals(userId) &&
                                readStatus.getChannelId().equals(channelId))
                .findFirst()
                .orElse(null);
    }
}