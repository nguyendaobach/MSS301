package com.mss301.quizservice.repository;

import com.mss301.quizservice.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document,String> {
    Page<Document> findByCategory(String category, Pageable pageable);
}
