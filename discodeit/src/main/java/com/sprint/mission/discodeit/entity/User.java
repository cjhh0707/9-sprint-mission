package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.UUID;

@Getter
public class User extends BaseEntity {
    private String displayName;
    private String email;
    private String password;
    private String status;
    private UUID profileImageId;

    public User(String displayName, String email, String password) {
        super();
        this.displayName = displayName;
        this.email = email;
        this.password = password;
        this.status = "온라인";
    }
//
//    public String getDisplayName() {
//        return displayName;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public String getStatus() {
//        return status;
//    }

    public void update(String displayName, String email, String password, String status) {
        this.displayName = displayName;
        this.email = email;
        this.password = password;
        this.status = status;
        setUpdatedAt();
    }

    public void setProfileImageId(UUID profileImageId) {
        this.profileImageId = profileImageId;
    }
}
