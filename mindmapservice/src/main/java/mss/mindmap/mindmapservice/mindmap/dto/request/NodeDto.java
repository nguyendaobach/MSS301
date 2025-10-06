package mss.mindmap.mindmapservice.mindmap.dto.request;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mss.mindmap.mindmapservice.mindmap.entity.Mindmap;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NodeDto {

    private UUID mindmap;

    private Integer version;

    private String label;
    private Float positionX;
    private Float positionY;
}
