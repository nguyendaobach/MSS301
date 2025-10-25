package mss.mindmap.mindmapservice.mindmap.service.implement;

import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import mss.mindmap.mindmapservice.mindmap.dto.request.ChangeEvent;
import mss.mindmap.mindmapservice.mindmap.dto.request.MindmapDto;
import mss.mindmap.mindmapservice.mindmap.entity.Mindmap;
import mss.mindmap.mindmapservice.mindmap.mapper.MindmapMapper;
import mss.mindmap.mindmapservice.mindmap.repository.IMindmapRepository;
import mss.mindmap.mindmapservice.mindmap.repository.NodeRepository;
import mss.mindmap.mindmapservice.mindmap.service.IMindmapService;
import mss.mindmap.mindmapservice.mindmap.util.HeaderExtractor;
import org.mapstruct.factory.Mappers;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MindmapService implements IMindmapService {
    private final IMindmapRepository mindmapRepository;

    private final MindmapMapper mapper;
    private final MindmapMapper mindmapMapper ;
    private final HeaderExtractor headerExtractor;


    @Override
    @Transactional
    public MindmapDto createMindmap(HttpServletRequest request, MindmapDto mindmapDto) {
        UUID userID = UUID.fromString(headerExtractor.getUserId(request).trim());
        Mindmap mindmap = Mindmap.builder()
                .title(mindmapDto.title())
                .description(mindmapDto.description())
                .userId(userID)
                .status("draft")
                .visibility("private")
                .createdAt(OffsetDateTime.now())
                .build();
        mindmapRepository.save(mindmap);
        MindmapDto response = mindmapMapper.toDto(mindmap);

        return response;
    }

    @Override
    @Transactional
    public MindmapDto updateMindmap(MindmapDto mindmapDto) {
        if (mindmapDto.mindMapId() == null) {
            throw new IllegalArgumentException("Mindmap id must be provided for update");
        }

        var existing = mindmapRepository.findById(mindmapDto.mindMapId())
            .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Mindmap not found: " + mindmapDto.mindMapId()));

        if (mindmapDto.title() != null) existing.setTitle(mindmapDto.title());
        if (mindmapDto.description() != null) existing.setDescription(mindmapDto.description());
        if (mindmapDto.status() != null) existing.setStatus(mindmapDto.status());
        if (mindmapDto.visibility() != null) existing.setVisibility(mindmapDto.visibility());


        var saved = mindmapRepository.save(existing);
        return mindmapMapper.toDto(saved);
    }

    @Override
    public void deleteMindmap(UUID mindmapId) {
        Optional<Mindmap> exist = mindmapRepository.findById(mindmapId);
        if (!exist.isPresent()) {
            throw new NullPointerException("Mindmap not found");
        }
        mindmapRepository.delete(exist.get());
    }

    @Override
    public List<MindmapDto> getMindmapByUserId(UUID userId) {
        Optional<List<Mindmap>> list = mindmapRepository.findByUserId(userId);
        if (!list.isPresent()) {
            return List.of();
        }
        return list.get().stream().map(mindmapMapper::toDto).collect(Collectors.toList());

    }

    @Override
    public MindmapDto getMindMapById(UUID id) {
        Optional<Mindmap> exist = mindmapRepository.findById(id);
        if (!exist.isPresent()) {
            throw new NullPointerException("Mindmap not found");
        }
        return mindmapMapper.toDto(exist.get());
    }


}