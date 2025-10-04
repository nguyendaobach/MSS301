package mss.mindmap.mindmapservice.mindmap.service.implement;

import lombok.RequiredArgsConstructor;
import mss.mindmap.mindmapservice.mindmap.dto.request.NodeDto;
import mss.mindmap.mindmapservice.mindmap.entity.Mindmap;
import mss.mindmap.mindmapservice.mindmap.entity.Nodes;
import mss.mindmap.mindmapservice.mindmap.repository.IMindmapRepository;
import mss.mindmap.mindmapservice.mindmap.repository.NodeRepository;
import mss.mindmap.mindmapservice.mindmap.service.INodeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NodeService implements INodeService {

    private final NodeRepository nodeRepository;
    private final IMindmapRepository mindmapRepository;

    @Override
    public Optional<List<Nodes>> getAllNodesByMindmapId(UUID mindmapId) {
        Mindmap mindmap = mindmapRepository.findById(mindmapId).orElse(null);
        return nodeRepository.findByMindmap(mindmap);
    }
}
