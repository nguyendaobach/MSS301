package com.mss301.quizservice.repository;

import com.mss301.quizservice.dto.response.QuizAttemptResponse;
import com.mss301.quizservice.entity.QuizAttempt;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, String> {

    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.userId = :userId ORDER BY qa.startedAt DESC")
    List<QuizAttempt> findByUserId(@Param("userId") String userId);

    @EntityGraph(attributePaths = "answers")
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quizAttemptId = :attemptId")
    Optional<QuizAttempt> findByIdWithAnswers(@Param("attemptId") String attemptId);
}

