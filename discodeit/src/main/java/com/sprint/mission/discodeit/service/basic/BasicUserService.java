package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

//    public BasicUserService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }

    @Override
    public UserResponse create(UserCreateRequest request) {
        //이메일, 사용자명 중복체크
        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + request.getEmail());
        }
        if (userRepository.findByDisplayName(request.getDisplayName()) != null) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다: " + request.getDisplayName());
        }

        //user 생성
        User user = new User(
                request.getDisplayName(),
                request.getEmail(),
                request.getPassword()
        );

        //프로필 이미지
        if (request.getProfileImage() != null) {
            BinaryContent profileImage = new BinaryContent(
                    request.getProfileImage().getFilename(),      // ✅ getFileName() → getFilename()
                    request.getProfileImage().getContentType(),
                    request.getProfileImage().getFileSize(),
                    request.getProfileImage().getContent()        // ✅ getData() → getContent()
            );
            profileImage.setProfileId(user.getId());
            binaryContentRepository.save(profileImage);
            user.setProfileImageId(profileImage.getId());
        }

        //user 저장
        userRepository.save(user);

        // user status 생성
        UserStatus userStatus = new UserStatus(user.getId());
        userStatusRepository.save(userStatus);

        //response 반환
        return new UserResponse(user, userStatus);
    }

    @Override
    public UserResponse findById(UUID id) {
        User user = userRepository.findById(id);
        if (user == null) {
            return null;
        }

        UserStatus userStatus = userStatusRepository.findByUserId(user.getId());
        return new UserResponse(user, userStatus);
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserStatus userStatus = userStatusRepository.findByUserId(user.getId());
                    return new UserResponse(user, userStatus);
                })
                .collect(Collectors.toList());
    }


    @Override
    public UserResponse update(UserUpdateRequest request) {
        User user = userRepository.findById(request.getUserId());
        if (user == null) {
            return null;
        }

        user.update(
                request.getDisplayName(),
                request.getEmail(),
                request.getPassword(),
                request.getStatus()
        );

        if (request.getProfileImage() != null) {
            if (user.getProfileImageId() != null) {
                binaryContentRepository.delete(user.getProfileImageId());
            }

            BinaryContent newProfileImage = new BinaryContent(
                    request.getProfileImage().getFilename(),
                    request.getProfileImage().getContentType(),
                    request.getProfileImage().getFileSize(),
                    request.getProfileImage().getContent()
            );
            newProfileImage.setProfileId(user.getId());
            binaryContentRepository.save(newProfileImage);
            user.setProfileImageId(newProfileImage.getId());
        }

        userRepository.save(user);

        UserStatus userStatus = userStatusRepository.findByUserId(user.getId());
        return new UserResponse(user, userStatus);
    }

    @Override
    public void delete(UUID id) {
        User user = userRepository.findById(id);
        if (user == null) {
            return;
        }

        if (user.getProfileImageId() != null) {
            binaryContentRepository.delete(user.getProfileImageId());
        }

        userStatusRepository.deleteByUserId(id);

        userRepository.delete(id);
    }
}
