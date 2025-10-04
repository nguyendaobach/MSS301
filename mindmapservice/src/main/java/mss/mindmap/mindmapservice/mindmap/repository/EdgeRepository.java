package mss.mindmap.mindmapservice.mindmap.repository;

import mss.mindmap.mindmapservice.mindmap.entity.Edges;
import mss.mindmap.mindmapservice.mindmap.entity.Mindmap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EdgeRepository extends JpaRepository<Edges, UUID> {
    Optional<List<Edges>> findByMindmap(Mindmap mindmap);
}
