package com.mss301.aiservice.controller;

import com.mss301.aiservice.dto.request.ExpandNodeRequest;
import com.mss301.aiservice.dto.request.MindMapRequest;
import com.mss301.aiservice.dto.request.RegenerateNodeRequest;
import com.mss301.aiservice.dto.response.ApiResponse;
import com.mss301.aiservice.dto.response.MindMapResponse;
import com.mss301.aiservice.service.MindMapService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@Slf4j
public class MindMapController {

    private final MindMapService mindmapService;

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<?>> generateMindmap(
            @Valid @RequestBody MindMapRequest request) {

        log.info("Generating mindmap for user: {}, prompt: {}",
                request.userId(), request.prompt());

        try {
            MindMapResponse response = mindmapService.generateMindMap(request);
            return ResponseEntity.ok(
                    ApiResponse.builder()
                            .success(true)
                            .message("Generated MindMap successfully")
                            .data(response)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error generating mindmap", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("Error: " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("/regenerate-node")
    public ResponseEntity<MindMapResponse.MindMapStructure.MindMapNode> regenerateNode(
            @RequestBody RegenerateNodeRequest request) {

        log.info("Regenerating node: {} for user: {}",
                request.nodeId(), request.userId());

        MindMapResponse.MindMapStructure.MindMapNode node = mindmapService.regenerateNode(request);
        return ResponseEntity.ok(node);
    }

    @PostMapping("/expand-node")
    public ResponseEntity<MindMapResponse> expandNode(
            @RequestBody ExpandNodeRequest request) {

        log.info("Expanding node: {} for user: {}",
                request.nodeId(), request.userId());

        MindMapResponse response = mindmapService.expandNode(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<?> getUserHistory(@PathVariable String userId) {
        return ResponseEntity.ok(mindmapService.getUserHistory(userId));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("MindMap Service is running");
    }
}