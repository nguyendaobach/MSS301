package mss.mindmap.mindmapservice.mindmap.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import mss.mindmap.mindmapservice.mindmap.dto.ApiResponse;
import mss.mindmap.mindmapservice.mindmap.dto.request.ChangeRequestDto;
import mss.mindmap.mindmapservice.mindmap.dto.request.MindmapDto;
import mss.mindmap.mindmapservice.mindmap.service.IEventService;
import mss.mindmap.mindmapservice.mindmap.service.IMindmapService;
import mss.mindmap.mindmapservice.mindmap.util.HeaderExtractor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("mindmap")
@RequiredArgsConstructor
public class MindMapController {


    private final HeaderExtractor headerExtractor;
    private final IMindmapService mindmapService;
    private final IEventService eventService;

    @PostMapping()
    public ApiResponse<MindmapDto> createMindmap(HttpServletRequest request, @RequestBody MindmapDto mindmapDto) {
        MindmapDto result = mindmapService.createMindmap(request, mindmapDto);

        return ApiResponse.<MindmapDto>builder()
                .code(HttpStatus.CREATED.value())
                .message("Mindmap created")
                .success(true)
                .data(result)
                .build();
    }

    @GetMapping("/user")
    public ApiResponse<List<MindmapDto>> userMindMaps (HttpServletRequest request) {
        UUID userId = UUID.fromString(headerExtractor.getUserId(request));
        List<MindmapDto> result = mindmapService.getMindmapByUserId(userId);

        return ApiResponse.<List<MindmapDto>>builder()
                .code(HttpStatus.OK.value())
                .message("")
                .success(true)
                .data(result)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<MindmapDto> mindMapDetail (@PathVariable UUID id) {
        MindmapDto result = mindmapService.getMindMapById(id);

        return ApiResponse.<MindmapDto>builder()
                .code(HttpStatus.OK.value())
                .message("")
                .success(true)
                .data(result)
                .build();
    }

    @PatchMapping("/{id}/event")
    public ApiResponse<MindmapDto> changeEvent (@PathVariable UUID id, @RequestBody ChangeRequestDto changeRequestDto) {
        eventService.applyChange(changeRequestDto, id);

        return ApiResponse.<MindmapDto>builder()
                .code(HttpStatus.OK.value())
                .message("success")
                .success(true)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<MindmapDto> deleteMindMap (@PathVariable UUID id) {
        mindmapService.deleteMindmap(id);

        return ApiResponse.<MindmapDto>builder()
                .code(HttpStatus.OK.value())
                .message("success")
                .success(true)
                .build();
    }

}
