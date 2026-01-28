package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final ChannelService channelService;

    public BasicMessageService(MessageRepository messageRepository, UserService userService, ChannelService channelService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message create(String content, UUID authorId, UUID channelId) {
        if (userService.findById(authorId) == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자");
        }
        if (channelService.findById(channelId) == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다");
        }

        Message message = new Message(content, authorId, channelId);

        return messageRepository.save(message);
    }

    @Override
    public Message findById(UUID id) {
        return messageRepository.findById(id);
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public List<Message> findByAuthorId(UUID authorId) {
        return messageRepository.findByAuthorId(authorId);
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        return messageRepository.findByChannelId(channelId);
    }

    @Override
    public Message update(UUID id, String content) {
        Message message = messageRepository.findById(id);
        if (message == null) {
            return null;
        }
        message.update(content);
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID id) {
        messageRepository.delete(id);
    }

    @Override
    public int deleteByAuthorId(UUID authorId) {
        return messageRepository.deleteByAuthorId(authorId);
    }

    @Override
    public int deleteByChannelId(UUID channelId) {
        return messageRepository.deleteByChannelId(channelId);
    }
}
