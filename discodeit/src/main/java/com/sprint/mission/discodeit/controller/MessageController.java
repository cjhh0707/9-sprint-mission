package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    // 메시지 전송 (첨부파일 포함)
    @RequestMapping(value = "", method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Message> sendMessage(
            @RequestParam("content") String content,
            @RequestParam("channelId") UUID channelId,
            @RequestParam("authorId") UUID authorId,
            @RequestPart(value = "attachments", required = false) MultipartFile[] attachments
    ) throws IOException {
        List<BinaryContentCreateRequest> binaryRequests = new ArrayList<>();
        if (attachments != null) {
            for (MultipartFile file : attachments) {
                binaryRequests.add(new BinaryContentCreateRequest(
                        file.getOriginalFilename(),
                        file.getContentType(),
                        file.getBytes()
                ));
            }
        }

        MessageCreateRequest request = new MessageCreateRequest(content, channelId, authorId);
        return ResponseEntity.ok(messageService.create(request, binaryRequests));
    }

    // 메시지 수정
    @RequestMapping(value = "", method = RequestMethod.PUT,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Message> updateMessage(
            @RequestParam("messageId") UUID messageId,
            @RequestParam("content") String content
    ) {
        return ResponseEntity.ok(messageService.update(messageId, new MessageUpdateRequest(content)));
    }

    // 메시지 삭제
    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteMessage(@RequestParam("messageId") UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.ok().build();
    }

    // 특정 채널의 메시지 목록 조회
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<Message>> getMessagesByChannel(@RequestParam("channelId") UUID channelId) {
        return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
    }
}
