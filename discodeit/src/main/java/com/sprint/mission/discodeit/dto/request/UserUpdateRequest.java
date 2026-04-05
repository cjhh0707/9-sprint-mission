package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    @Size(min = 2, max = 50, message = "Username must be between 2 and 50 characters")
    String newUsername,

    @Email(message = "Email must be valid")
    String newEmail,

    @Size(min = 6, message = "Password must be at least 6 characters")
    String newPassword
) {

}
