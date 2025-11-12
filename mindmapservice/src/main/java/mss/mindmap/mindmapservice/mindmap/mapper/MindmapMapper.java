package mss.mindmap.mindmapservice.mindmap.mapper;

import mss.mindmap.mindmapservice.mindmap.dto.request.MindmapDto;
import mss.mindmap.mindmapservice.mindmap.entity.Mindmap;
import org.mapstruct.*;


@Mapper(componentModel = "spring")
public interface MindmapMapper {

    @Mapping(target = "mindMapId", source = "mindmap.id")
    @Mapping(target = "createdAt", source = "createdAt")
    MindmapDto toDto(Mindmap mindmap);


}
