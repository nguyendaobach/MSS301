package mss.mindmap.mindmapservice.mindmap.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Table(name = "mindmap_edge")
@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Edges extends BaseEntity {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "mindmapId")
    private Mindmap mindmap;

    private Integer version;

    private String label;

    @ManyToOne
    @JoinColumn(name = "souceNodeId")
    private Nodes sourceNode;

    @ManyToOne
    @JoinColumn(name = "targetNodeId")
    private Nodes targetNode;





}
