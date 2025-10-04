package mss.mindmap.mindmapservice.mindmap.service;

import mss.mindmap.mindmapservice.mindmap.dto.request.EdgeDto;
import mss.mindmap.mindmapservice.mindmap.dto.request.NodeDto;
import mss.mindmap.mindmapservice.mindmap.entity.Edges;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IEdgeService {
    Optional<List<Edges>> getEdgesByMindMap(UUID mindMapUuid);
}
