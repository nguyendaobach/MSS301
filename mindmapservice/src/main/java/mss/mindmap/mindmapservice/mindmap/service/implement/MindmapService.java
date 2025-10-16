package mss.mindmap.mindmapservice.mindmap.service.implement;

import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mss.mindmap.mindmapservice.mindmap.dto.request.ChangeEvent;
import mss.mindmap.mindmapservice.mindmap.dto.request.MindmapDto;
import mss.mindmap.mindmapservice.mindmap.entity.Mindmap;
import mss.mindmap.mindmapservice.mindmap.mapper.MindmapMapper;
import mss.mindmap.mindmapservice.mindmap.repository.IMindmapRepository;
import mss.mindmap.mindmapservice.mindmap.repository.NodeRepository;
import mss.mindmap.mindmapservice.mindmap.service.IMindmapService;
import org.mapstruct.factory.Mappers;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

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


    @Override
    public MindmapDto createMindmap(MindmapDto mindmapDto) {
        Mindmap mindmap = Mindmap.builder()
                .title(mindmapDto.title())
                .description(mindmapDto.description())
                .userId(mindmapDto.userId())
                .status(mindmapDto.status())
                .visibility(mindmapDto.visibility())
                .createdAt(OffsetDateTime.now())
                .build();
        mindmapRepository.save(mindmap);
        MindmapDto response = mindmapMapper.toDto(mindmap);

        return response;
    }

    @Override
    public MindmapDto updateMindmap(MindmapDto mindmapDto) {
        return null;
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
