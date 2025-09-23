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
public class QuizAnswerKey {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @OneToOne
    @JoinColumn(name = "quiz_id", nullable = false, unique = true)
    Quizzes quiz;


    @ElementCollection
    @CollectionTable(
            name = "quiz_answer_key_map",
            joinColumns = @JoinColumn(name = "quiz_answer_key_id")
    )
    @MapKeyColumn(name = "question_index")
    @Column(name = "answer")
    Map<Integer, String> answers = new HashMap<>();

}
