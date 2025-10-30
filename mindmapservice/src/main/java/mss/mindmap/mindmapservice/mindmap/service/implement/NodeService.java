package mss.mindmap.mindmapservice.mindmap.service.implement;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import mss.mindmap.mindmapservice.mindmap.dto.request.NodeDto;
import mss.mindmap.mindmapservice.mindmap.entity.Mindmap;
import mss.mindmap.mindmapservice.mindmap.entity.Nodes;
import mss.mindmap.mindmapservice.mindmap.repository.IMindmapRepository;
import mss.mindmap.mindmapservice.mindmap.repository.NodeRepository;
import mss.mindmap.mindmapservice.mindmap.service.INodeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NodeService implements INodeService {

    private final NodeRepository nodeRepository;
    private final IMindmapRepository iMindmapRepository;
    private final IMindmapRepository mindmapRepository;

    @Override
    public Optional<List<Nodes>> getAllNodesByMindmapId(UUID mindmapId) {
        Mindmap mindmap = mindmapRepository.findById(mindmapId).orElseThrow(() -> new EntityNotFoundException("Mindmap " +mindmapId + " not found"));
        return nodeRepository.findByMindmap(mindmap);
    }

    @Override
    public void createNode(NodeDto nodeDto) {
        try{
            Mindmap mindmap = iMindmapRepository.findById(nodeDto.getMindmap()).orElseThrow(() -> new EntityNotFoundException("MindMap not found"));
            Nodes node = Nodes.builder()
                    .id(nodeDto.getNodeId())
                    .label(nodeDto.getLabel())
                    .version(nodeDto.getVersion())
                    .positionY(nodeDto.getPositionY())
                    .positionX(nodeDto.getPositionX())
                    .mindmap(mindmap)
                    .createdAt(OffsetDateTime.now())
                    .build();
            nodeRepository.save(node);
        }catch(Exception e){

        }
    }

    @Override
    public void updateNode(UUID nodeId, NodeDto dto) {
        var node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new EntityNotFoundException("Node not found: " + nodeId));

        // Partial update: chỉ set khi DTO có giá trị
        if (dto.getLabel() != null) node.setLabel(dto.getLabel());
        if (dto.getPositionX() != null) node.setPositionX(dto.getPositionX());
        if (dto.getPositionY() != null) node.setPositionY(dto.getPositionY());

        nodeRepository.save(node); // trong @Transactional: flush khi commit
    }

    @Override
    public void updateNodePosition(UUID nodeId, NodeDto dto) {
        if (dto.getPositionX() == null || dto.getPositionY() == null) {
            throw new IllegalArgumentException("positionX & positionY are required");
        }
        var node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new EntityNotFoundException("Node not found: " + nodeId));
        node.setPositionX(dto.getPositionX());
        node.setPositionY(dto.getPositionY());
        nodeRepository.save(node);
    }

    @Override
    @Transactional
    public void deleteNode(UUID nodeId) {
        // Kiểm tra node có tồn tại không
        if (!nodeRepository.existsById(nodeId)) {
            throw new EntityNotFoundException("Node not found: " + nodeId);
        }
        
        // Xóa node - các edges sẽ tự động bị xóa nhờ ON DELETE CASCADE
        nodeRepository.deleteById(nodeId);
    }
}




