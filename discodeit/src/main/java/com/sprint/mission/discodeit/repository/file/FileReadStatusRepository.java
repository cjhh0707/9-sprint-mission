package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.config.RepositoryConfig;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "file"
)
public class FileReadStatusRepository implements ReadStatusRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileReadStatusRepository(RepositoryConfig config) {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), config.getFileDirectory(), ReadStatus.class.getSimpleName());
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
    public ReadStatus save(ReadStatus readStatus) {
        Path path = resolvePath(readStatus.getId());
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(readStatus);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return readStatus;
    }

    @Override
    public ReadStatus findById(UUID id) {
        Path path = resolvePath(id);
        if (!Files.exists(path)) {
            return null;
        }

        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (ReadStatus) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    public List<ReadStatus> findAll() {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (FileInputStream fis = new FileInputStream(path.toFile());
                             ObjectInputStream ois = new ObjectInputStream(fis)) {
                            return (ReadStatus) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
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

    @Override
    public List<ReadStatus> findByChannelId(UUID channelId) {
        return findAll().stream()
                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public int deleteByChannelId(UUID channelId) {
        List<ReadStatus> readStatuses = findByChannelId(channelId);
        for (ReadStatus readStatus : readStatuses) {
            delete(readStatus.getId());
        }
        return readStatuses.size();
    }

    @Override
    public List<ReadStatus> findByUserId(UUID userId) {
        return findAll().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .toList();
    }

    @Override
    public ReadStatus findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return findAll().stream()
                .filter(readStatus ->
                        readStatus.getUserId().equals(userId) &&
                                readStatus.getChannelId().equals(channelId))
                .findFirst()
                .orElse(null);
    }
}
