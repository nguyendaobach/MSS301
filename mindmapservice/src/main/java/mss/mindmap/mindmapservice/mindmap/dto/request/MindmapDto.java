package mss.mindmap.mindmapservice.mindmap.dto.request;

import jakarta.persistence.Column;
import mss.mindmap.mindmapservice.mindmap.entity.Mindmap;

import java.io.Serializable;
import java.util.UUID;


public record MindmapDto(
        UUID userId,
        String title,
        String description,
        String status ,
        String visibility
) {}
