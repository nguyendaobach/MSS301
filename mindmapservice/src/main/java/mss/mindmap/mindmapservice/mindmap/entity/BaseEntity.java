package mss.mindmap.mindmapservice.mindmap.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.UUID;

@MappedSuperclass
@Getter @Setter
@SuperBuilder        // <<— quan trọng
@NoArgsConstructor   // JPA cần
@AllArgsConstructor
public abstract class BaseEntity {



    @Column(name = "deleted_at", nullable = true)
    private OffsetDateTime deletedAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = true)
    private OffsetDateTime updatedAt = OffsetDateTime.now();
}
