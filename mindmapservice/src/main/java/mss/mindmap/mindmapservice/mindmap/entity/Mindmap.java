package mss.mindmap.mindmapservice.mindmap.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "mindmap")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Mindmap extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = true)
    private String status = "draft"; // draft | published | archived

    @Column(nullable = false)
    private String visibility = "private"; // private | link | org

//    @ElementCollection
//    @CollectionTable(name = "mindmap_shared_with", joinColumns = @JoinColumn(name = "mindmap_id"))
//    @Column(name = "user_or_email")
//    private List<String> sharedWith;


}
