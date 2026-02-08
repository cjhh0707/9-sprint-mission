package com.sprint.mission.discodeit.dto.request;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String displayName;
    private String password;

    public LoginRequest(String displayName, String password) {
        this.displayName = displayName;
        this.password = password;
    }

}
