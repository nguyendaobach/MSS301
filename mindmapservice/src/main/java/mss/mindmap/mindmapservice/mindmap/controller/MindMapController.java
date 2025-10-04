package mss.mindmap.mindmapservice.mindmap.controller;

import lombok.RequiredArgsConstructor;
import mss.mindmap.mindmapservice.mindmap.dto.ApiResponse;
import mss.mindmap.mindmapservice.mindmap.dto.request.MindmapDto;
import mss.mindmap.mindmapservice.mindmap.service.IMindmapService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/mindmap")
@RequiredArgsConstructor
public class MindMapController {


    private final IMindmapService mindmapService;

    @PostMapping()
    public ApiResponse<MindmapDto> createMindmap(@RequestBody MindmapDto mindmapDto) {
        MindmapDto result = mindmapService.createMindmap(mindmapDto);

        return ApiResponse.<MindmapDto>builder()
                .code(HttpStatus.CREATED.value())
                .message("mindmap created")
                .data(result)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<MindmapDto> mindMapDetail (@PathVariable UUID id) {
        MindmapDto result = mindmapService.getMindMapById(id);

        return ApiResponse.<MindmapDto>builder()
                .code(HttpStatus.OK.value())
                .message("mindmap created")
                .data(result)
                .build();
    }

}
