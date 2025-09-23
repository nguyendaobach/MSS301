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
public class QuizAttempts {
    enum Status {
        IN_PROGRESS,
        COMPLETED,
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String quizAttemptId;
    String userId;
    Timestamp startedAt;
    Timestamp completedAt;
    Double score;
    @Enumerated(EnumType.STRING)
    Status status;
    @OneToMany(mappedBy = "quizAttempt", cascade = CascadeType.ALL, orphanRemoval = true)
    List<QuizAttemptAnswer> answers = new ArrayList<>();


}
