package com.mss301.aiservice.dto.response;

import com.mss301.aiservice.dto.RetrievedChunkDto;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record MindMapResponse(
        MindMapStructure mindMap,
        List<RetrievedChunkDto> sourceChunks,
        LocalDateTime generatedAt
) {

    @Builder
    public record MindMapStructure(
            String title,
            List<MindMapNode> nodes
    ) {

        @Builder
        public record MindMapNode(
                String id,
                String label,
                Integer level,
                String parent,
                List<String> children,
                String description
        ) {
        }
    }
}
