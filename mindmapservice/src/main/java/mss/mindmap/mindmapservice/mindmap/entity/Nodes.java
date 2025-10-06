package mss.mindmap.mindmapservice.mindmap.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mindmap_node")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Nodes extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "mindmapId")
    private Mindmap mindmap;

    private Integer version;

    private String label;
    private Float positionX;
    private Float positionY;




}
