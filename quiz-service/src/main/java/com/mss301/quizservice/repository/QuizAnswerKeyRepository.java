package com.mss301.quizservice.repository;

import com.mss301.quizservice.entity.QuizAnswerKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizAnswerKeyRepository extends JpaRepository<QuizAnswerKey,String> {
}
