package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class BinaryContentDuplicateException extends BinaryContentException {

  public BinaryContentDuplicateException(UUID id) {
    super(ErrorCode.BINARY_CONTENT_DUPLICATE, Map.of("binaryContentId", id));
  }
}
