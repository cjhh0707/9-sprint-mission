package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController implements ChannelApi {

  private final ChannelService channelService;

  // 공개 채널 생성
  @PostMapping(value = "/public", consumes = MediaType.APPLICATION_JSON_VALUE)
  @Override
  public ResponseEntity<Channel> createPublicChannel(
      @RequestBody PublicChannelCreateRequest request
  ) {
    Channel createdchannel = channelService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdchannel);
  }

  // 비공개 채널 생성
  @PostMapping(value = "/private", consumes = MediaType.APPLICATION_JSON_VALUE)
  @Override
  public ResponseEntity<Channel> createPrivateChannel(
      @RequestBody PrivateChannelCreateRequest request
  ) {
    Channel createdchannel = channelService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdchannel);
  }

  // 공개 채널 수정
  @PatchMapping(value = "{channelId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @Override
  public ResponseEntity<Channel> update(
      @PathVariable("channelId") UUID channelId,
      @RequestBody PublicChannelUpdateRequest request
  ) {
    Channel updatedchannel = channelService.update(channelId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedchannel);
  }

  // 채널 삭제
  @DeleteMapping("{channelId}")
  @Override
  public ResponseEntity<Void> delete(
      @PathVariable("channelId") UUID channelId
  ) {
    channelService.delete(channelId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT).build();
  }

  // 특정 사용자가 볼 수 있는 채널 목록 조회
  @GetMapping
  @Override
  public ResponseEntity<List<ChannelDto>> findAllByUserId(
      @RequestParam("userId") UUID userId
  ) {
    List<ChannelDto> channels = channelService.findAllByUserId(userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(channels);
  }
}
