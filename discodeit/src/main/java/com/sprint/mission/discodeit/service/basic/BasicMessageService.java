package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
//import lombok.var;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;

//    public BasicMessageService(MessageRepository messageRepository, UserService userRepository, ChannelService channelRepository) {
//        this.messageRepository = messageRepository;
//        this.userRepository = userRepository;
//        this.channelRepository = channelRepository;
//    }

    @Override
    public MessageResponse create(MessageCreateRequest request) {
        //검증
        if (userRepository.findById(request.getAuthorId()) == null) {
            throw new IllegalArgumentException("User not found");
        }
        if (channelRepository.findById(request.getChannelId()) == null) {
            throw new IllegalArgumentException("Channel not found");
        }

        //메시지 생성
        Message message = new Message(
                request.getContent(),
                request.getAuthorId(),
                request.getChannelId()
        );

        //첨부파일 처리
        if (request.getAttachments() != null && !request.getAttachments().isEmpty()) {
            List<UUID> attachmentIds = new ArrayList<>();

            for (var attachmentRequest : request.getAttachments()) {
                BinaryContent attachment = new BinaryContent(
                        attachmentRequest.getFilename(),
                        attachmentRequest.getContentType(),
                        attachmentRequest.getFileSize(),
                        attachmentRequest.getContent()
                );

                List<UUID> messageIds = new ArrayList<>();
                messageIds.add(message.getId());
                attachment.setAttachmentId(messageIds);

                binaryContentRepository.save(attachment);
                attachmentIds.add(attachment.getId());
            }

            message.setAttachmentIds(attachmentIds);
        }

        messageRepository.save(message);

        return new MessageResponse(message);
    }

    @Override
    public MessageResponse findById(UUID id) {
        Message message = messageRepository.findById(id);
        if (message == null) {
            return null;
        }
        return new MessageResponse(message);
    }

    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        return messageRepository.findByChannelId(channelId).stream()
                .map(MessageResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public MessageResponse update(MessageUpdateRequest request) {
        Message message = messageRepository.findById(request.getMessageId());
        if (message == null) {
            return null;
        }

        message.update(request.getContent());
        messageRepository.save(message);

        return new MessageResponse(message);
    }

    @Override
    public void delete(UUID id) {
        Message message = messageRepository.findById(id);
        if (message == null) {
            return;
        }

        if (message.getAttachmentIds() != null && !message.getAttachmentIds().isEmpty()) {
            binaryContentRepository.deleteAll(message.getAttachmentIds());
        }

        messageRepository.delete(id);
    }

    @Override
    public List<MessageResponse> findByAuthorId(UUID authorId) {
        return messageRepository.findByAuthorId(authorId).stream()
                .map(MessageResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public int deleteByAuthorId(UUID authorId) {
        List<Message> messages = messageRepository.findByAuthorId(authorId);
        for (Message message : messages) {
            if (message.getAttachmentIds() != null && !message.getAttachmentIds().isEmpty()) {
                binaryContentRepository.deleteAll(message.getAttachmentIds());
            }
        }
        return messageRepository.deleteByAuthorId(authorId);
    }

    @Override
    public int deleteByChannelId(UUID channelId) {
        List<Message> messages = messageRepository.findByChannelId(channelId);
        for (Message message : messages) {
            if (message.getAttachmentIds() != null && !message.getAttachmentIds().isEmpty()) {
                binaryContentRepository.deleteAll(message.getAttachmentIds());
            }
        }
        return messageRepository.deleteByChannelId(channelId);
    }

}
