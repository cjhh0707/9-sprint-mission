package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    //생성
    MessageResponse create(MessageCreateRequest request);

    //단건 조회
    MessageResponse findById(UUID id);
    //전체
    List<MessageResponse> findAllByChannelId(UUID channelId);
//    //사용자별 검색
//    List<Message> findByAuthorId(UUID authorId);
//    //채널별 검색
//    List<Message> findByChannelId(UUID channelId);

    //수정
    MessageResponse update(MessageUpdateRequest request);

    //삭제
    void delete(UUID id);

    List<MessageResponse> findByAuthorId(UUID authorId);
    int deleteByAuthorId(UUID authorId);
    int deleteByChannelId(UUID channelId);
}
