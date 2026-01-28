package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;

public interface MessageService {

    //생성
    Message create(String content, UUID authorId, UUID channelId);

    //단건 조회
    Message findById(UUID id);
    //전체
    List<Message> findAll();
    //사용자별 검색
    List<Message> findByAuthorId(UUID authorId);
    //채널별 검색
    List<Message> findByChannelId(UUID channelId);

    //수정
    Message update(UUID id, String content);

    //삭제
    void delete(UUID id);

    int deleteByAuthorId(UUID authorId);

    int deleteByChannelId(UUID channelId);
}
