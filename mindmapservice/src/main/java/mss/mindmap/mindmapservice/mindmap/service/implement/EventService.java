package mss.mindmap.mindmapservice.mindmap.service.implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mss.mindmap.mindmapservice.mindmap.dto.request.ChangeEvent;
import mss.mindmap.mindmapservice.mindmap.dto.request.ChangeRequestDto;
import mss.mindmap.mindmapservice.mindmap.dto.request.EdgeDto;
import mss.mindmap.mindmapservice.mindmap.dto.request.NodeDto;
import mss.mindmap.mindmapservice.mindmap.service.IEdgeService;
import mss.mindmap.mindmapservice.mindmap.service.IEventService;
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


    @Override
@Transactional
    public void applyChange(ChangeRequestDto changeRequestDto, UUID mapId) {
        for (ChangeEvent event : changeRequestDto.getChanges()) {
            switch (event.type) {

                // ======== NODE ======== //
                case ADD_NODE -> {
                    NodeDto dto = NodeDto.builder()
                            .nodeId(event.getNodeId())
                            .label(event.label)
                            .positionX(event.x)
                            .positionY(event.y)
                            .mindmap(event.mindMapid)
                            .build();
                    nodeService.createNode(dto);
                    log.info("ADD_NODE");
                    break;
                }

                case UPDATE_NODE -> {
                    NodeDto dto = NodeDto.builder()
                            .label(event.label)
                            .positionX(event.x)
                            .positionY(event.y)
                            .build();
                    nodeService.updateNode(event.nodeId, dto);
                    break;
                }

                case MOVE_NODE -> {
                    if (event.x == null || event.y == null)
                        throw new IllegalArgumentException("MOVE_NODE cần x,y");
                    NodeDto dto = NodeDto.builder()
                            .positionX(event.x)
                            .positionY(event.y)
                            .build();
                    nodeService.updateNodePosition(event.nodeId, dto);
                    break;
                }

                case REMOVE_NODE -> {
                    nodeService.deleteNode(event.nodeId);
                    break;
                }

                // ======== EDGE ======== //
                case ADD_EDGE -> {
                    if (event.sourceNode == null || event.targetNode == null)
                        throw new IllegalArgumentException("ADD_EDGE cần source, target");
                    EdgeDto edgeDto = EdgeDto.builder()
                            .edgeId(event.edgeId)
                            .sourceNode(event.sourceNode)
                            .targetNode(event.targetNode)
                            .build();
                    edgeService.createEdge(edgeDto);
                    break;

                }

                case REMOVE_EDGE -> {
                    edgeService.deleteNode(event.edgeId);
                    break;
                }

//            // ======== MAP ======== //
//            case UPDATE_MAP_TITLE -> {
//                mindmapRepository.updateTitle(mapId, op.title);
//                log.info("[UPDATE_MAP_TITLE] {}", op.title);
//            }
//
//            case UPDATE_MAP_TAGS -> {
//                mindmapRepository.updateTags(mapId, op.tags);
//                log.info("[UPDATE_MAP_TAGS] {}", op.tags);
//            }
//
//            case UPDATE_MAP_STATUS -> {
//                mindmapRepository.updateStatus(mapId, op.status);
//                log.info("[UPDATE_MAP_STATUS] {}", op.status);
//            }

                default -> throw new UnsupportedOperationException("Unsupported ChangeType: " + event.type);
            }
        }

    }
}
