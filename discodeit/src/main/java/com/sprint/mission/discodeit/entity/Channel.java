package com.sprint.mission.discodeit.entity;

import lombok.Getter;

@Getter
public class Channel extends BaseEntity {
    private String channelName;
    private String description;
    private ChannelType type;

    public Channel(String channelName, String description) {
        super();
        this.channelName = channelName;
        this.description = description;
        this.type = ChannelType.PUBLIC; //기본 Public으로
    }

    public Channel(ChannelType type) {
        super();
        this.type = type;
        this.channelName = null;
        this.description = null;
    }

//    public String getChannelName() {
//        return channelName;
//    }
//
//    public String getDescription() {
//        return description;
//    }

    public void update(String channelName, String description) {
        if (this.type == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("Private channels cannot be updated");
        }
        this.channelName = channelName;
        this.description = description;
        setUpdatedAt();
    }
}
