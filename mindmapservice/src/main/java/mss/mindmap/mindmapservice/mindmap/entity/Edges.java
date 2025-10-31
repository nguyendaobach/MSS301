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
    @JoinColumn(name = "souce_node_id", nullable = false, foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (souce_node_id) REFERENCES mindmap_node(id) ON DELETE CASCADE"))
    private Nodes sourceNode;

    @ManyToOne
    @JoinColumn(name = "target_node_id", nullable = false, foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (target_node_id) REFERENCES mindmap_node(id) ON DELETE CASCADE"))
    private Nodes targetNode;

    private String sourceHandle;
    private String targetHandle;





}
