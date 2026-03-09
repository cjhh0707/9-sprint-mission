package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BinaryContentMapper {

  public BinaryContentDto toDto(BinaryContent entity) {
//    if (entity == null) {
//      //return null;
//    }
    return new BinaryContentDto(
        entity.getId(),
        entity.getFileName(),
        entity.getSize(),
        entity.getContentType()
    );
  }
}
