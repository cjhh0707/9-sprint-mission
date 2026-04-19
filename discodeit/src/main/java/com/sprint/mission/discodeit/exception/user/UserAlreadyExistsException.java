package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserAlreadyExistsException extends UserException {

    public UserAlreadyExistsException(ErrorCode errorCode) {
        super(errorCode);
    }

    public static UserAlreadyExistsException withEmail(String email) {
        UserAlreadyExistsException exception = new UserAlreadyExistsException(ErrorCode.DUPLICATE_EMAIL);
        exception.addDetail("email", email);
        return exception;
    }

    public static UserAlreadyExistsException withUsername(String username) {
        UserAlreadyExistsException exception = new UserAlreadyExistsException(ErrorCode.DUPLICATE_USERNAME);
        exception.addDetail("username", username);
        return exception;
    }
}
