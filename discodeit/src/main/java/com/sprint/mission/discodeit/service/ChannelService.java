package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    //생성
    ChannelResponse createPublicChannel(PublicChannelCreateRequest request);
    ChannelResponse createPrivateChannel(PrivateChannelCreateRequest request);

    //단건 조회
    ChannelResponse findById(UUID id);
    //전체 조회
    List<ChannelResponse> findAllByUserId(UUID userId);

    //수정
    ChannelResponse update(ChannelUpdateRequest request);

    //삭제
    void delete(UUID id);

}
