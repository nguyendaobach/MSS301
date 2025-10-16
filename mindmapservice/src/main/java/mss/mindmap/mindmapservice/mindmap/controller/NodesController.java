package mss.mindmap.mindmapservice.mindmap.controller;


import lombok.RequiredArgsConstructor;
import mss.mindmap.mindmapservice.mindmap.dto.ApiResponse;
import mss.mindmap.mindmapservice.mindmap.entity.Edges;
import mss.mindmap.mindmapservice.mindmap.entity.Nodes;
import mss.mindmap.mindmapservice.mindmap.service.IEdgeService;
import mss.mindmap.mindmapservice.mindmap.service.INodeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("node")
@RequiredArgsConstructor
public class NodesController {

    private final INodeService nodeService;

    @GetMapping("mindmap/{mindmapId}")
    public ApiResponse<Optional<List<Nodes>>> getNodeByMindMap(@PathVariable UUID mindmapId) {
        Optional<List<Nodes>> nodes = nodeService.getAllNodesByMindmapId(mindmapId);

        return ApiResponse.<Optional<List<Nodes>>>builder()
                .code(HttpStatus.OK.value())
                .message("Get All Nodes")
                .data(nodes)
                .build();
    }
}
