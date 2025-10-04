package mss.mindmap.mindmapservice.mindmap.mapper;

import mss.mindmap.mindmapservice.mindmap.dto.request.MindmapDto;
import mss.mindmap.mindmapservice.mindmap.entity.Mindmap;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MindmapMapper {

    MindmapDto toDto(Mindmap mindmap);
    Mindmap fromDto(MindmapDto mindmapDto);
}
