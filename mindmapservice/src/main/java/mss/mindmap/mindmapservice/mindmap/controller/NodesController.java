package mss.mindmap.mindmapservice.mindmap.controller;


import lombok.RequiredArgsConstructor;
import mss.mindmap.mindmapservice.mindmap.dto.ApiResponse;
import mss.mindmap.mindmapservice.mindmap.entity.Edges;
import mss.mindmap.mindmapservice.mindmap.service.IEdgeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/node")
@RequiredArgsConstructor
public class NodesController {

    private final IEdgeService nodeService;

    @GetMapping("mindmap/{mindmapId}")
    public ApiResponse<Optional<List<Edges>>> getNodeByMindMap(@PathVariable UUID mindmapId) {
        Optional<List<Edges>> edges = nodeService.getEdgesByMindMap(mindmapId);

        return ApiResponse.<Optional<List<Edges>>>builder()
                .code(HttpStatus.CREATED.value())
                .message("Edge")
                .data(edges)
                .build();
    }
}
