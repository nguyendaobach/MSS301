package mss.mindmap.mindmapservice.mindmap.service.implement;

import lombok.RequiredArgsConstructor;
import mss.mindmap.mindmapservice.mindmap.dto.request.EdgeDto;
import mss.mindmap.mindmapservice.mindmap.entity.Edges;
import mss.mindmap.mindmapservice.mindmap.entity.Mindmap;
import mss.mindmap.mindmapservice.mindmap.entity.Nodes;
import mss.mindmap.mindmapservice.mindmap.repository.EdgeRepository;
import mss.mindmap.mindmapservice.mindmap.repository.IMindmapRepository;
import mss.mindmap.mindmapservice.mindmap.repository.NodeRepository;
import mss.mindmap.mindmapservice.mindmap.service.IEdgeService;
import mss.mindmap.mindmapservice.mindmap.service.INodeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EdgeService implements IEdgeService {

    private final EdgeRepository edgeRepository;
    private final NodeRepository nodeRepository;
    private final IMindmapRepository mindmapRepository;

    @Override
    public Optional<List<Edges>> getEdgesByMindMap(UUID mindMapid) {
        Mindmap mindmap = mindmapRepository.findById(mindMapid).orElse(null);
        return edgeRepository.findByMindmap(mindmap);
    }

    @Override
    public void createEdge(EdgeDto edgeDto) {
        Nodes sourceNode = nodeRepository.findById(edgeDto.getSourceNode()).orElse(null);
        Nodes targetNode = nodeRepository.findById(edgeDto.getTargetNode()).orElse(null);

        Edges edges =  Edges.builder()
                .sourceNode(sourceNode)
                .targetNode(targetNode)
                .build();
        edgeRepository.save(edges);

    }

    @Override
    public void updateEdge(UUID edgeId, EdgeDto edgeDto) {

    }

    @Override
    public void deleteNode(UUID edgeId) {
        edgeRepository.deleteById(edgeId);
    }
}
