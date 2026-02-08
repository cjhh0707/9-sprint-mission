package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponse;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatusResponse create(ReadStatusCreateRequest request) {
        if (userRepository.findById(request.getUserId()) == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (channelRepository.findById(request.getChannelId()) == null) {
            throw new IllegalArgumentException("Channel not found");
        }

        ReadStatus existing = readStatusRepository.findByUserIdAndChannelId(request.getUserId(), request.getChannelId());
        if (existing != null) {
            throw new IllegalArgumentException("already exists");
        }

        ReadStatus readStatus = new ReadStatus(
                request.getUserId(),
                request.getChannelId()
        );

        readStatusRepository.save(readStatus);

        return new ReadStatusResponse(readStatus);
    }

    @Override
    public ReadStatusResponse findById(UUID id) {
        ReadStatus readStatus = readStatusRepository.findById(id);
        if (readStatus == null) {
            return null;
        }
        return new ReadStatusResponse(readStatus);
    }

    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        return readStatusRepository.findByUserId(userId).stream()
                .map(ReadStatusResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public ReadStatusResponse update(ReadStatusUpdateRequest request) {
        ReadStatus readStatus = readStatusRepository.findById(request.getReadStatusId());
        if (readStatus == null) {
            return null;
        }

        readStatus.updateLastRead(request.getLastRead());
        readStatusRepository.save(readStatus);

        return new ReadStatusResponse(readStatus);
    }

    @Override
    public void delete(UUID id) {
        readStatusRepository.delete(id);
    }
}
