package mss.mindmap.mindmapservice.mindmap.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "mindmap_node")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Nodes extends BaseEntity {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "mindmapId")
    private Mindmap mindmap;

    private Integer version;

    private String label;
    private Float positionX;
    private Float positionY;




}
