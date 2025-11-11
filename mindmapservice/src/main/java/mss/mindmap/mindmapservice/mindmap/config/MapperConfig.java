package mss.mindmap.mindmapservice.mindmap.config;

import mss.mindmap.mindmapservice.mindmap.mapper.MindmapMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public MindmapMapper mindmapMapper() {
        return Mappers.getMapper(MindmapMapper.class);
    }
}
