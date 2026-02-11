package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "jcf",
        matchIfMissing = true
)
public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> data = new HashMap<>();

    @Override
    public Message save(Message message) {
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public List<Message> findByAuthorId(UUID authorId) {
        return data.values().stream().filter(message -> message.getAuthorId().equals(authorId)).collect(Collectors.toList());
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        return data.values().stream().filter(message -> message.getChannelId().equals(channelId)).collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }

    @Override
    public int deleteByAuthorId(UUID authorId) {
        List<UUID> idsToDelete = data.values().stream()
                .filter(message -> message.getAuthorId().equals(authorId))
                .map(Message::getId)
                .collect(Collectors.toList());

        idsToDelete.forEach(data::remove);
        return idsToDelete.size();
    }

    @Override
    public int deleteByChannelId(UUID channelId) {
        List<UUID> idsToDelete = data.values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .map(Message::getId)
                .collect(Collectors.toList());

        idsToDelete.forEach(data::remove);
        return idsToDelete.size();
    }

    @Override
    public Instant findLastMessageTimeByChannelId(UUID channelId) {
        return findByChannelId(channelId).stream()
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null);
    }

}
