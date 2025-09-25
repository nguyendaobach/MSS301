package com.mss301.quizservice.repository;

import com.mss301.quizservice.dto.response.QuizAttemptResponse;
import com.mss301.quizservice.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, String> {
    List<QuizAttempt> findByUserId(String userId);
}
