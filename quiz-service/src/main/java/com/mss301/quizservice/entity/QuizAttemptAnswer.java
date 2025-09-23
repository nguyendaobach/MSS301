package com.mss301.quizservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class QuizAttemptAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_attempt_id", nullable = false)
    QuizAttempts quizAttempt;

    @ElementCollection
    @CollectionTable(
            name = "quiz_attempt_answer_map",
            joinColumns = @JoinColumn(name = "quiz_attempt_answer_id")
    )
    @MapKeyColumn(name = "question_index")
    @Column(name = "answer")
    Map<Integer, String> answers = new HashMap<>();
}

