package com.mss301.vectorservice.dto.request;

import com.mss301.vectorservice.dto.DocumentMetadataDto;
import com.mss301.vectorservice.dto.TextChunkDto;

import java.util.List;

public record EmbedRequest(
        List<TextChunkDto> chunks,
        DocumentMetadataDto metadata
) {
}
