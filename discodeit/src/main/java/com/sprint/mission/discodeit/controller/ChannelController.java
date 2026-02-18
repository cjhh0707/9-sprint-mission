package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    // 공개 채널 생성
    @RequestMapping(value = "/public", method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Channel> createPublicChannel(
            @RequestParam("name") String name,
            @RequestParam("description") String description
    ) {
        return ResponseEntity.ok(channelService.create(new PublicChannelCreateRequest(name, description)));
    }

    // 비공개 채널 생성
    @RequestMapping(value = "/private", method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Channel> createPrivateChannel(
            @RequestParam("participantIds") List<UUID> participantIds
    ) {
        return ResponseEntity.ok(channelService.create(new PrivateChannelCreateRequest(participantIds)));
    }

    // 공개 채널 수정
    @RequestMapping(value = "", method = RequestMethod.PUT,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Channel> updateChannel(
            @RequestParam("channelId") UUID channelId,
            @RequestParam("name") String name,
            @RequestParam("description") String description
    ) {
        return ResponseEntity.ok(channelService.update(channelId, new PublicChannelUpdateRequest(name, description)));
    }

    // 채널 삭제
    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteChannel(@RequestParam("channelId") UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.ok().build();
    }

    // 특정 사용자가 볼 수 있는 채널 목록 조회
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<ChannelDto>> getChannelsByUserId(@RequestParam("userId") UUID userId) {
        return ResponseEntity.ok(channelService.findAllByUserId(userId));
    }
}
