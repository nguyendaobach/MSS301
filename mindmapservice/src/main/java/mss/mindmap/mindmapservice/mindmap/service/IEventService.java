package mss.mindmap.mindmapservice.mindmap.service;

import mss.mindmap.mindmapservice.mindmap.dto.request.ChangeEvent;
import mss.mindmap.mindmapservice.mindmap.dto.request.ChangeRequestDto;

import java.util.UUID;

public interface IEventService {
     void applyChange(ChangeRequestDto changeRequestDto, UUID mapId);
}
