package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    @Override
    public UserResponse login(LoginRequest request) {
        User user = userRepository.findByDisplayName(request.getDisplayName());

        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        if (!user.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return new UserResponse(user, null);
    }

}
