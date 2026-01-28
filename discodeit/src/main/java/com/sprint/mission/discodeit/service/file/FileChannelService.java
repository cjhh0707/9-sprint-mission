package com.sprint.mission.discodeit.service.file;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

public class FileChannelService implements ChannelService {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileChannelService() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", Channel.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e){
                throw new RuntimeException(e);
            }
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }


    @Override
    public Channel create(String channelName, String description) {
        Channel channel = new Channel(channelName, description);
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
        Channel channelNullable = null;
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
                    ) {
                channelNullable = (Channel) ois.readObject();
            } catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        return channelNullable;
    }

    @Override
    public List<Channel> findAll() {
        try {
            return Files.list(DIRECTORY).filter(path -> path.toString().endsWith(EXTENSION)).map(path -> {
                try (
                        FileInputStream fis = new FileInputStream(path.toFile());
                        ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                    return (Channel) ois.readObject();
                } catch (ClassNotFoundException | IOException e) {
                    throw new RuntimeException(e);
                }
            }).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Channel update(UUID id, String channelName, String description) {
        Channel channel = findById(id);
        if (channel == null) {
            return null;
        }
        channel.update(channelName, description);

        Path path = resolvePath(id);
        try(
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
    public void delete(UUID id) {
        Path path = resolvePath(id);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
