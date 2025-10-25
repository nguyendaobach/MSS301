package mss.mindmap.mindmapservice.mindmap.service.implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mss.mindmap.mindmapservice.mindmap.dto.request.*;
import mss.mindmap.mindmapservice.mindmap.repository.IMindmapRepository;
import mss.mindmap.mindmapservice.mindmap.service.IEdgeService;
import mss.mindmap.mindmapservice.mindmap.service.IEventService;
import mss.mindmap.mindmapservice.mindmap.service.IMindmapService;
import mss.mindmap.mindmapservice.mindmap.service.INodeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class EventService implements IEventService {

    private final IEdgeService edgeService;
    private final INodeService nodeService;
    private final IMindmapService mindmapService;
    private final IMindmapRepository mindmapRepository;


    @Override
    @Transactional
    public void applyChange(ChangeRequestDto changeRequestDto, UUID mapId) {
        for (ChangeEvent event : changeRequestDto.getChanges()) {
            switch (event.type) {

                // ======== NODE ======== //
                case NODE_ADD -> {
                    NodeDto dto = NodeDto.builder()
                            .nodeId(UUID.fromString(event.getNodeId()))
                            .label(event.label)
                            .positionX(event.x)
                            .positionY(event.y)
                            .mindmap(mapId)
                            .build();
                    nodeService.createNode(dto);
                    log.info("ADD_NODE");
                    break;
                }

                case NODE_UPDATE -> {
                    NodeDto dto = NodeDto.builder()
                            .label(event.label)
                            .positionX(event.x)
                            .positionY(event.y)
                            .build();
                    nodeService.updateNode(UUID.fromString(event.nodeId), dto);
                    log.info("UPDATE_NODE");
                    break;
                }

//                case MOVE_NODE -> {
//                    if (event.x == null || event.y == null)
//                        throw new IllegalArgumentException("MOVE_NODE cần x,y");
//                    NodeDto dto = NodeDto.builder()
//                            .positionX(event.x)
//                            .positionY(event.y)
//                            .build();
//                    nodeService.updateNodePosition(event.nodeId, dto);
//                    break;
//                }

                case NODE_DELETE -> {
                    nodeService.deleteNode(UUID.fromString(event.nodeId));
                    break;
                }

                // ======== EDGE ======== //
                case EDGE_ADD -> {
                    if (event.sourceNode == null || event.targetNode == null)
                        throw new IllegalArgumentException("ADD_EDGE cần source, target");
                    EdgeDto edgeDto = EdgeDto.builder()
                            .edgeId(UUID.fromString(event.edgeId))
                            .sourceNode(UUID.fromString(event.sourceNode))
                            .targetNode(UUID.fromString(event.targetNode))
                            .mindmapId(mapId)
                            .build();
                    edgeService.createEdge(edgeDto);
                    break;

                }

                case EDGE_DELETE -> {
                    edgeService.deleteNode(UUID.fromString(event.edgeId));
                    break;
                }

            // ======== MAP ======== //
                case CHANGE_TITLE  -> {
                    MindmapDto dto = MindmapDto.builder()
                            .mindMapId(mapId)
                            .title(event.mindMapName)
                            .build();
                    mindmapService.updateMindmap(dto);
                    break;

                }


                default -> throw new UnsupportedOperationException("Unsupported ChangeType: " + event.type);
            }
        }

    }
}
