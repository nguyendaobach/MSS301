package mss.mindmap.mindmapservice.mindmap.repository;

import mss.mindmap.mindmapservice.mindmap.entity.Edges;
import mss.mindmap.mindmapservice.mindmap.entity.Mindmap;
import mss.mindmap.mindmapservice.mindmap.entity.Nodes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EdgeRepository extends JpaRepository<Edges, UUID> {
    Optional<List<Edges>> findByMindmap(Mindmap mindmap);

//    @Query("SELECT e FROM Edges e " +
//            "WHERE (e.sourceNode = :deletedNode OR e.targetNode = :deletedNode) " +
//            "AND e.mindmap = :mindmap")
//    Optional<List<Edges>> findBySourceNodeOrTargetNode(Nodes sourceNode, Nodes targetNode);


    int deleteEdgesBySourceNodeOrTargetNode(Nodes sourceNode, Nodes targetNode);
}
