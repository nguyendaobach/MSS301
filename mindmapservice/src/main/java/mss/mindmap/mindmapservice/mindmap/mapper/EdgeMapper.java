package mss.mindmap.mindmapservice.mindmap.mapper;

import mss.mindmap.mindmapservice.mindmap.dto.request.EdgeDto;
import mss.mindmap.mindmapservice.mindmap.entity.Edges;

public class EdgeMapper {
    public EdgeDto mapToDto(Edges edges) {
        EdgeDto dto = EdgeDto
                .builder()
                .edgeId(edges.getId())
                .label(edges.getLabel())
                .sourceNode(edges.getSourceNode().getId())
                .targetNode(edges.getTargetNode().getId())

                .build();
        return dto;
    }


}
