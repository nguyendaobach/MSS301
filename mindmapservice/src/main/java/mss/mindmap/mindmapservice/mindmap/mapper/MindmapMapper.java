package mss.mindmap.mindmapservice.mindmap.mapper;

import mss.mindmap.mindmapservice.mindmap.dto.request.MindmapDto;
import mss.mindmap.mindmapservice.mindmap.entity.Mindmap;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MindmapMapper {

    @Mapping(target = "mindMapId", source = "mindmap.id")
    MindmapDto toDto(Mindmap mindmap);
    Mindmap toEntity(MindmapDto mindmapDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true) // KHÔNG đè id
    void updateEntityFromDto(MindmapDto dto, @MappingTarget Mindmap entity);

}
