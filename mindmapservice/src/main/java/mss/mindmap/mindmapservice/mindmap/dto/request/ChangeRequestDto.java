package mss.mindmap.mindmapservice.mindmap.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ChangeRequestDto {
    List<ChangeEvent> changes;
}
