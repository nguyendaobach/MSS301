package mss.mindmap.mindmapservice.mindmap.service;

import mss.mindmap.mindmapservice.mindmap.dto.request.NodeDto;
import mss.mindmap.mindmapservice.mindmap.entity.Nodes;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface INodeService {
    Optional<List<Nodes>> getAllNodesByMindmapId(UUID mindmapId);

    void createNode(NodeDto nodeDto);
    void updateNode(UUID nodeId, NodeDto nodeDto);
    void updateNodePosition(UUID nodeId, NodeDto nodeDto);
    void deleteNode(UUID nodeId);

}
