package mss.mindmap.mindmapservice.mindmap.controller;

import lombok.RequiredArgsConstructor;
import mss.mindmap.mindmapservice.mindmap.dto.ApiResponse;
import mss.mindmap.mindmapservice.mindmap.dto.request.ChangeRequestDto;
import mss.mindmap.mindmapservice.mindmap.dto.request.MindmapDto;
import mss.mindmap.mindmapservice.mindmap.service.IEventService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("event")
@RequiredArgsConstructor
public class EventController {

    private final IEventService eventService;


    @PostMapping("/{mindmapId}")
    public ApiResponse<?> createMindmap(@PathVariable UUID mindmapId, @RequestBody ChangeRequestDto  changeRequestDto) {
        eventService.applyChange(changeRequestDto, mindmapId );

        return ApiResponse.<MindmapDto>builder()
                .code(HttpStatus.OK.value())
                .message("Update Success")
                .build();
    }

   @GetMapping("hello")
    public String hello() {
        return "hello";
   }



}
