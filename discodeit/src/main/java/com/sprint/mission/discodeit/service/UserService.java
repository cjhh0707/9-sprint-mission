package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface UserService {
    //생성
    UserResponse create(UserCreateRequest request);

    //단건 조회
    UserResponse findById(UUID id);
    //전체 조회
    List<UserResponse> findAll();

    //수정
    UserResponse update(UserUpdateRequest request);

    //삭제
    void delete(UUID id);

}
