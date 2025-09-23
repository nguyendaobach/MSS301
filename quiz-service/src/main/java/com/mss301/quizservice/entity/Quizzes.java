package com.mss301.quizservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Quizzes {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String quizId;
    String name;
    String description;
    String category;
    String pdfUrl;
    String createdBy;
    Timestamp  createdDate;
    Integer duration;
    Double price;
    @OneToMany(mappedBy = "quizAttemptId", cascade = CascadeType.ALL, orphanRemoval = true)
    List<QuizAttempts> attempts = new ArrayList<>();

}
