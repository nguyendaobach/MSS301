package mss.mindmap.mindmapservice.mindmap.service.implement;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import mss.mindmap.mindmapservice.mindmap.dto.request.EdgeDto;
import mss.mindmap.mindmapservice.mindmap.entity.Edges;
import mss.mindmap.mindmapservice.mindmap.entity.Mindmap;
import mss.mindmap.mindmapservice.mindmap.entity.Nodes;
import mss.mindmap.mindmapservice.mindmap.repository.EdgeRepository;
import mss.mindmap.mindmapservice.mindmap.repository.MindmapRepository;
import mss.mindmap.mindmapservice.mindmap.repository.NodeRepository;
import mss.mindmap.mindmapservice.mindmap.service.IEdgeService;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EdgeService implements IEdgeService {

    private final EdgeRepository edgeRepository;
    private final NodeRepository nodeRepository;
    private final MindmapRepository mindmapRepository;


    @Override
    public Optional<List<Edges>> getEdgesByMindMap(UUID mindMapid) {
        Mindmap mindmap = mindmapRepository
                .findById(mindMapid)
                .orElseThrow(() -> new EntityNotFoundException("Mindmap " + mindMapid + " not found"));
        Optional<List<Edges>> edges =  edgeRepository.findByMindmap(mindmap);
        return edges;
    }

    @Override
    public void createEdge(EdgeDto edgeDto) {
        try{
            Nodes sourceNode = nodeRepository.findById(edgeDto.getSourceNode())
                    .orElseThrow(() -> new EntityNotFoundException("Source node not found"));

            Nodes targetNode = nodeRepository.findById(edgeDto.getTargetNode())
                    .orElseThrow(() -> new EntityNotFoundException("Targe node not found"));

            Mindmap mindmap = mindmapRepository.findById(edgeDto.getMindmapId())
                    .orElseThrow(() -> new EntityNotFoundException("MindMap not found"));

            Edges edges =  Edges.builder()
                    .id(edgeDto.getEdgeId())
                    .sourceNode(sourceNode)
                    .targetNode(targetNode)
                    .sourceHandle(edgeDto.getSourceHandle())
                    .targetHandle(edgeDto.getTargetHandle())
                    .mindmap(mindmap)
                    .createdAt(OffsetDateTime.now())
                    .build();
            edgeRepository.save(edges);
        }catch(Exception e){

        }


    }

    @Override
    public void updateEdge(UUID edgeId, EdgeDto edgeDto) {

    }

    @Override
    public void deleteEdge(UUID edgeId) {
        edgeRepository.deleteById(edgeId);
    }

    @Override
    public void deleteEdgesBySourceNodeOrTargetNode(UUID nodeId) {
        Optional<Nodes> existNode = nodeRepository.findById(nodeId);
        if (!existNode.isPresent()) {
            return ;
        }
        int count = edgeRepository.deleteEdgesBySourceNodeOrTargetNode(existNode.get(), existNode.get());
    }
}
