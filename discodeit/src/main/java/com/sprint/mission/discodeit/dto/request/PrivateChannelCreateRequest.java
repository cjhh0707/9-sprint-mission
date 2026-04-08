package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
    @NotEmpty(message = "Participant list must not be empty")
    List<UUID> participantIds
) {

}
