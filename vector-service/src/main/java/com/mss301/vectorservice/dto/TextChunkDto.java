package com.mss301.vectorservice.dto;

public record TextChunkDto(
        Integer index,
        String text,
        Integer wordCount
) {
}
