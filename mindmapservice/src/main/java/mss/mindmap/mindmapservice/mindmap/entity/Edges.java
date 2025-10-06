package mss.mindmap.mindmapservice.mindmap.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Table(name = "mindmap_edge")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Edges extends BaseEntity {
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
