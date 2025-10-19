package com.mss301.documentservice.dto.request;

import com.mss301.documentservice.dto.DocumentMetadataDTO;
import com.mss301.documentservice.dto.TextChunkDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record EmbedRequest(
        List<TextChunkDTO> chunks,
        DocumentMetadataDTO metadata
) {
}
