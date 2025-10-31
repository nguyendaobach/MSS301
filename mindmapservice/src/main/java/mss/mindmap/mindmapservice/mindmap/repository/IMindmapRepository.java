package mss.mindmap.mindmapservice.mindmap.repository;

import mss.mindmap.mindmapservice.mindmap.entity.Mindmap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IMindmapRepository extends JpaRepository<Mindmap, UUID> {
    Optional<List<Mindmap>> findByUserId(UUID userId);

}
