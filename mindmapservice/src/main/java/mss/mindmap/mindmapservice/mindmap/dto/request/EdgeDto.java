package mss.mindmap.mindmapservice.mindmap.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EdgeDto {
    private Integer version;

    private String label;

    private UUID sourceNode;

    private UUID targetNode;

}
