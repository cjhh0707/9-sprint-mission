package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readstatus")
@RequiredArgsConstructor
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    // 읽음 상태 생성
    @RequestMapping(value = "", method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReadStatus> createReadStatus(
            @RequestParam("userId") UUID userId,
            @RequestParam("channelId") UUID channelId
    ) {
        return ResponseEntity.ok(readStatusService.create(new ReadStatusCreateRequest(userId, channelId, Instant.now())));
    }

    // 읽음 상태 업데이트 (마지막 읽은 시간 갱신)
    @RequestMapping(value = "", method = RequestMethod.PUT,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReadStatus> updateReadStatus(
            @RequestParam("readStatusId") UUID readStatusId
    ) {
        return ResponseEntity.ok(readStatusService.update(readStatusId, new ReadStatusUpdateRequest(Instant.now())));
    }

    // 사용자의 읽음 상태 목록 조회
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatus>> getReadStatuses(@RequestParam("userId") UUID userId) {
        return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
    }
}