package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

//    public BasicChannelService(ChannelRepository channelRepository) {
//        this.channelRepository = channelRepository;
//    }

    @Override
    public ChannelResponse createPublicChannel(PublicChannelCreateRequest request) {
        //public 채널 생성
        Channel channel = new Channel(
                request.getChannelName(),
                request.getDescription()
        );

        //저장
        channelRepository.save(channel);

        //response 반환
        return new ChannelResponse(channel, null);
    }

    @Override
    public ChannelResponse createPrivateChannel(PrivateChannelCreateRequest request) {
        //private 채널 생성
        Channel channel = new Channel(ChannelType.PRIVATE);

        //저장
        channelRepository.save(channel);

        //참여 user별 readstatus생성
        for (UUID userId : request.getUserIds()) {
            ReadStatus readStatus = new ReadStatus(userId, channel.getId());
            readStatusRepository.save(readStatus);
        }

        //response 반환
        return new ChannelResponse(channel, null, request.getUserIds());
    }

    @Override
    public ChannelResponse findById(UUID id) {
        Channel channel = channelRepository.findById(id);
        if (channel == null) {
            return null;
        }

        //최근 메시지 시간 조회
        Instant lastMessageAt = messageRepository.findLastMessageTimeByChannelId(id);

        // public 채널
        if (channel.getType() == ChannelType.PUBLIC) {
            return new ChannelResponse(channel, lastMessageAt);
        }

        // private 채널 - 참여 사용자 id 목록 조회
        List<UUID> participantUserIds = readStatusRepository.findByChannelId(id)
                .stream()
                .map(ReadStatus::getUserId)
                .collect(Collectors.toList());

        return new ChannelResponse(channel, lastMessageAt, participantUserIds);
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        List<Channel> allChannels = channelRepository.findAll();

        //사용자의 readstatus 목록
        List<UUID> userChannelIds = readStatusRepository.findByUserId(userId)
                .stream()
                .map(ReadStatus::getChannelId)
                .collect(Collectors.toList());

        return allChannels.stream()
                .filter(channel -> {
                    if (channel.getType() == ChannelType.PUBLIC) {
                        return true;
                    }
                    return userChannelIds.contains(channel.getId());
                })
                .map(channel -> {
                    Instant lastMessageAt = messageRepository.findLastMessageTimeByChannelId(channel.getId());

                    if (channel.getType() == ChannelType.PUBLIC) {
                        return new ChannelResponse(channel, lastMessageAt);
                    }

                    List<UUID> participantUserIds = readStatusRepository.findByChannelId(channel.getId())
                            .stream()
                            .map(ReadStatus::getUserId)
                            .collect(Collectors.toList());

                    return new ChannelResponse(channel, lastMessageAt, participantUserIds);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ChannelResponse update(ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(request.getChannelId());
        if (channel == null) {
            return null;
        }

        channel.update(request.getChannelName(), request.getDescription());

        channelRepository.save(channel);

        Instant lastMessageAt = messageRepository.findLastMessageTimeByChannelId(channel.getId());
        return new ChannelResponse(channel, lastMessageAt);
    }

    @Override
    public void delete(UUID id) {
        Channel channel = channelRepository.findById(id);
        if (channel == null) {
            return;
        }

        messageRepository.deleteByChannelId(id);

        readStatusRepository.deleteByChannelId(id);

        channelRepository.delete(id);
    }
}
