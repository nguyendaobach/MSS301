package com.mss301.quizservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
public class QuizAttempt {
    public enum Status {
        IN_PROGRESS,
        COMPLETED,
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String quizAttemptId;
    @ManyToOne
    @JoinColumn(name = "quiz_id")
    @JsonBackReference
    Quiz quiz;
    String userId;
    Timestamp startedAt;
    Timestamp completedAt;
    Double score;
    @Enumerated(EnumType.STRING)
    Status status;
    @OneToMany(mappedBy = "quizAttempt", fetch =  FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    List<QuizAttemptAnswer> answers = new ArrayList<>();


}
