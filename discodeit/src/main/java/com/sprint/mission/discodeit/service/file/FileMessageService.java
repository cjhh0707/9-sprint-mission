package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileMessageService implements MessageService {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";
    private final UserService userService;
    private final ChannelService channelService;

    public FileMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", Message.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public Message create(String content, UUID authorId, UUID channelId) {
        if (userService.findById(authorId) == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자 입니다");
        }
        if (channelService.findById(channelId) == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다");
        }

        Message message = new Message(content, authorId, channelId);
        Path path = resolvePath(message.getId());
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
                ) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return message;
    }

    @Override
    public Message findById(UUID id) {
        Message messageNullable = null;
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
                    ) {
                messageNullable = (Message) ois.readObject();
            } catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        return messageNullable;
    }

    @Override
    public List<Message> findAll() {
        try {
            return Files.list(DIRECTORY).filter(path -> path.toString().endsWith(EXTENSION)).map(
                    path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                                ) {
                            return (Message) ois.readObject();
                        } catch (ClassNotFoundException | IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
            ).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Message> findByAuthorId(UUID authorId) {
        return findAll().stream().filter(message -> message.getAuthorId().equals(authorId)).collect(Collectors.toList());
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        return findAll().stream().filter(message -> message.getChannelId().equals(channelId)).collect(Collectors.toList());
    }

    @Override
    public Message update(UUID id, String content) {
        Message message = findById(id);
        if (message == null) {
            return null;
        }
        message.update(content);

        Path path = resolvePath(id);

        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
                ) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return message;
    }

    @Override
    public void delete(UUID id) {
        Path path = resolvePath(id);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int deleteByAuthorId(UUID authorId) {
        List<Message> messages = findByAuthorId(authorId);
        messages.forEach(message -> delete(message.getId()));
        return messages.size();
    }

    @Override
    public int deleteByChannelId(UUID channelId) {
        List<Message> messages = findByChannelId(channelId);
        messages.forEach(message -> delete(message.getId()));
        return messages.size();
    }
}
