package mss.mindmap.mindmapservice.mindmap.service;

import mss.mindmap.mindmapservice.mindmap.dto.request.MindmapDto;


import java.util.List;
import java.util.UUID;

public interface IMindmapService {
    MindmapDto createMindmap(MindmapDto mindmapDto);
    MindmapDto updateMindmap(MindmapDto mindmapDto);
    void deleteMindmap(UUID mindmapId);
    List<MindmapDto> getMindmapByUserId(UUID userId);
    MindmapDto getMindMapById(UUID id);
}
