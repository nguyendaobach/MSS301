package com.mss301.quizservice.repository;


import com.mss301.quizservice.entity.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, String> {
    Page<Quiz> findByCategory(String category, Pageable pageable);
}
