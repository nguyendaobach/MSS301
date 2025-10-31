package mss.mindmap.mindmapservice.mindmap.service;

import mss.mindmap.mindmapservice.mindmap.dto.request.EdgeDto;
import mss.mindmap.mindmapservice.mindmap.dto.request.NodeDto;
import mss.mindmap.mindmapservice.mindmap.entity.Edges;
import mss.mindmap.mindmapservice.mindmap.entity.Nodes;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IEdgeService {
    Optional<List<Edges>> getEdgesByMindMap(UUID mindMapUuid);

    void createEdge(EdgeDto edgeDto);
    void updateEdge(UUID edgeId, EdgeDto edgeDto);
    void deleteEdge(UUID edgeId);
    void deleteEdgesBySourceNodeOrTargetNode(UUID nodeId);
}
