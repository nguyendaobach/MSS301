package com.mss301.quizservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
public class DocumentReview {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
     String id;

     int rating;
     String comment;
     String reviewer;
     Timestamp reviewedAt;

    @ManyToOne
    @JoinColumn(name = "document_id")
    @JsonBackReference
    private Document document;
}

