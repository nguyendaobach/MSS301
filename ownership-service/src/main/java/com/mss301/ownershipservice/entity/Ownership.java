package com.mss301.ownershipservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "ownership",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"ownerId", "assetType", "assetId"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Ownership {
    public enum AssetType {
        DOCUMENT,
        MINDMAP,
        PREMIUM,
        QUIZ
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String ownerId;

    @Enumerated(EnumType.STRING)
    AssetType assetType;

    String assetId;

    LocalDateTime createdAt;
}
