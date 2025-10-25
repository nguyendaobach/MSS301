package mss.mindmap.mindmapservice.mindmap.dto.request;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;
import mss.mindmap.mindmapservice.mindmap.entity.Mindmap;

import java.io.Serializable;
import java.util.UUID;

@Builder
public record MindmapDto(
        UUID mindMapId,
        String title,
        String description,
        String status ,
        String visibility
) {}
