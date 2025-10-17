package com.mss301.aiservice.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "mindmap_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MindMapHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true, nullable = false)
    String historyId;

    @Column(nullable = false)
    String userId;

    @Column(nullable = false, length = 1000)
    String prompt;

    @Column(nullable = false, columnDefinition = "TEXT")
    String mindMapJson;

    @Column(length = 1000)
    String sourceDocuments; // Comma-separated document IDs

    @Column(nullable = false)
    LocalDateTime createdAt;
}
