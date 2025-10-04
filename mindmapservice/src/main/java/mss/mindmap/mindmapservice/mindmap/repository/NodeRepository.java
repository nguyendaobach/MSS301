package mss.mindmap.mindmapservice.mindmap.repository;

import mss.mindmap.mindmapservice.mindmap.entity.Mindmap;
import mss.mindmap.mindmapservice.mindmap.entity.Nodes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NodeRepository extends JpaRepository<Nodes, UUID> {
    Optional<List<Nodes>> findByMindmap(Mindmap mindmap);
}
