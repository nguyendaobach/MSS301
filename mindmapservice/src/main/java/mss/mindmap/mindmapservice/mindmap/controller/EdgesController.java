package mss.mindmap.mindmapservice.mindmap.controller;


import lombok.RequiredArgsConstructor;
import mss.mindmap.mindmapservice.mindmap.dto.ApiResponse;
import mss.mindmap.mindmapservice.mindmap.dto.request.MindmapDto;
import mss.mindmap.mindmapservice.mindmap.entity.Edges;
import mss.mindmap.mindmapservice.mindmap.repository.EdgeRepository;
import mss.mindmap.mindmapservice.mindmap.service.IEdgeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/edge")
@RequiredArgsConstructor
public class EdgesController {
    private final IEdgeService edgeService;

    @GetMapping("mindmap/{mindmapId}")
    public ApiResponse<Optional<List<Edges>>> getEdgesByMindmap(@PathVariable UUID mindmapId) {
        Optional<List<Edges>> edges = edgeService.getEdgesByMindMap(mindmapId);

        return ApiResponse.<Optional<List<Edges>>>builder()
                .code(HttpStatus.CREATED.value())
                .message("Edge")
                .data(edges)
                .build();
    }
}
