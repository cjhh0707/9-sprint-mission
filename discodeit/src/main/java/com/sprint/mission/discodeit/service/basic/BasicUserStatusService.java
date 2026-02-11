package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusResponse;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatusResponse create(UserStatusCreateRequest request) {
        if (userRepository.findById(request.getUserId()) == null) {
            throw new IllegalArgumentException("User not found");
        }

        UserStatus existing = userStatusRepository.findByUserId(request.getUserId());
        if (existing != null) {
            throw new IllegalArgumentException("already exists");
        }

        UserStatus userStatus = new UserStatus(request.getUserId());

        userStatusRepository.save(userStatus);

        return new UserStatusResponse(userStatus);
    }

    @Override
    public UserStatusResponse findById(UUID id) {
        UserStatus userStatus = userStatusRepository.findByUserId(id);
        if (userStatus == null) {
            return null;
        }
        return new UserStatusResponse(userStatus);
    }

    @Override
    public List<UserStatusResponse> findAll() {
        return userStatusRepository.findAll().stream().map(UserStatusResponse::new).collect(Collectors.toList());
    }

    @Override
    public UserStatusResponse update(UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findByUserId(request.getUserStatusId());
        if (userStatus == null) {
            return null;
        }

        userStatus.updateLastLogin(request.getLastLogin());
        userStatusRepository.save(userStatus);

        return new UserStatusResponse(userStatus);
    }

    @Override
    public UserStatusResponse updateByUserId(UUID userId, Instant lastLogin) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId);
        if (userStatus == null) {
            return null;
        }

        userStatus.updateLastLogin(lastLogin);
        userStatusRepository.save(userStatus);

        return new UserStatusResponse(userStatus);
    }

    @Override
    public void delete(UUID id) {
        userRepository.delete(id);
    }
}
