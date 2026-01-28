package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

public class FileUserService implements UserService {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileUserService() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", User.class.getSimpleName());
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
    public User create(String displayName, String email, String phoneNumber) {
        User user = new User(displayName, email, phoneNumber);
        Path path = resolvePath(user.getId());
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
                ) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public User findById(UUID id) {
        User userNullable = null;
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
                    ) {
                userNullable = (User) ois.readObject();
            } catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        return userNullable;
    }

    @Override
    public List<User> findAll() {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (User) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User update(UUID id, String displayName, String email, String phoneNumber, String status) {
        User user = findById(id);
        if (user == null) {
            return null;
        }
        user.update(displayName, email, phoneNumber, status);

        Path path = resolvePath(id);
        try(
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
                ) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return user;
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
