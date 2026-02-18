package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    // 사용자 등록
    @RequestMapping(value = "", method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> createUser(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) throws IOException {
        UserCreateRequest request = new UserCreateRequest(username, email, password);

        Optional<BinaryContentCreateRequest> profileRequest = Optional.empty();
        if (profileImage != null && !profileImage.isEmpty()) {
            profileRequest = Optional.of(new BinaryContentCreateRequest(
                    profileImage.getOriginalFilename(),
                    profileImage.getContentType(),
                    profileImage.getBytes()
            ));
        }

        return ResponseEntity.ok(userService.create(request, profileRequest));
    }

    // 사용자 정보 수정
    @RequestMapping(value = "", method = RequestMethod.PUT,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> updateUser(
            @RequestParam("userId") UUID userId,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "password", required = false) String password,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) throws IOException {
        UserUpdateRequest request = new UserUpdateRequest(username, email, password);

        Optional<BinaryContentCreateRequest> profileRequest = Optional.empty();
        if (profileImage != null && !profileImage.isEmpty()) {
            profileRequest = Optional.of(new BinaryContentCreateRequest(
                    profileImage.getOriginalFilename(),
                    profileImage.getContentType(),
                    profileImage.getBytes()
            ));
        }
        return ResponseEntity.ok(userService.update(userId, request, profileRequest));
    }

    // 사용자 삭제
    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(@RequestParam("userId") UUID userId) {
        userService.delete(userId);
        return ResponseEntity.ok().build();
    }

    // 사용자 조회
    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    // 사용자 온라인 상태 업데이트
    @RequestMapping(value = "/status", method = RequestMethod.PUT)
    public ResponseEntity<Void> updateUserStatus(@RequestParam("userId") UUID userId) {
        userStatusService.updateByUserId(userId, new UserStatusUpdateRequest(Instant.now()));
        return ResponseEntity.ok().build();
    }
}
