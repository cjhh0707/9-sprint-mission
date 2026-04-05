package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Size;

public record PublicChannelUpdateRequest(
    @Size(max = 100, message = "Channel name must not exceed 100 characters")
    String newName,

    @Size(max = 500, message = "Description must not exceed 500 characters")
    String newDescription
) {

}
