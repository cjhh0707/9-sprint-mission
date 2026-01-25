package com.sprint.mission.discodeit.entity;

public class User extends BaseEntity {
    private String displayName;
    private String email;
    private String phoneNumber;
    private String status;

    public User(String displayName, String email, String phoneNumber) {
        super();
        this.displayName = displayName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.status = "온라인";
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public void update(String displayName, String email, String phoneNumber, String status) {
        this.displayName = displayName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.status = status;
        setUpdatedAt();
    }
}
