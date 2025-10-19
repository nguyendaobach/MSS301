package com.mss301.documentservice.dto;

import lombok.Builder;

@Builder
public record TextChunkDTO(
        Integer index,
        String text,
        Integer wordCount
) {
}
