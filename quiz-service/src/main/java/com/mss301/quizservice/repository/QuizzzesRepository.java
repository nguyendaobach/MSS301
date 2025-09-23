package com.mss301.quizservice.repository;


import com.mss301.quizservice.entity.Quizzes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizzzesRepository extends JpaRepository<Quizzes, String> {
}
