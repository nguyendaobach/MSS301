package com.mss301.quizservice.repository;

import com.mss301.quizservice.entity.QuizAttemptAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizAttemptsAnswerRepository extends JpaRepository<QuizAttemptAnswer,String> {
}
