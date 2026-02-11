package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContentResponse create(BinaryContentRequest request) {
        BinaryContent binaryContent = new BinaryContent(
                request.getFilename(),
                request.getContentType(),
                request.getFileSize(),
                request.getContent()
        );

        binaryContentRepository.save(binaryContent);

        return new BinaryContentResponse(binaryContent);
    }

    @Override
    public BinaryContentResponse findById(UUID id) {
        BinaryContent binaryContent = binaryContentRepository.findById(id);
        if (binaryContent == null) {
            return null;
        }
        return new BinaryContentResponse(binaryContent);
    }

    @Override
    public List<BinaryContentResponse> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAllByIdIn(ids).stream()
                .map(BinaryContentResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        binaryContentRepository.delete(id);
    }
}
