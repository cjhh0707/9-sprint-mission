package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.BinaryContent;
import lombok.Getter;

@Getter
public class UserCreateRequest {
    private String displayName;
    private String email;
    private String password;

    private BinaryContentRequest profileImage;

    public UserCreateRequest(String displayName, String email, String password) {
        this.displayName = displayName;
        this.email = email;
        this.password = password;
    }

    public UserCreateRequest(String displayName, String email, String password,BinaryContentRequest profileImage) {
        this.displayName = displayName;
        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
    }
}
