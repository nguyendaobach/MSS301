package com.mss301.quizservice.repository;

import com.mss301.quizservice.entity.QuizAttempts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizAttemptsRepository extends JpaRepository<QuizAttempts, String> {
}
