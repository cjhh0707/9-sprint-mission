package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileChannelRepository() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", Channel.class.getSimpleName());
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
    public Channel save(Channel channel) {
        Path path = resolvePath(channel.getId());
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
                ) {
            oos.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        Path path = resolvePath(id);
        if (!Files.exists(path)) {
            return null;
        }

        try (
                FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
                ) {
            return (Channel) ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            return null;
        }
    }

    @Override
    public List<Channel> findAll() {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                                ) {
                            return (Channel) ois.readObject();
                        } catch (ClassNotFoundException | IOException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();
        } catch (IOException e) {
            return new ArrayList<>();
        }
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
}
