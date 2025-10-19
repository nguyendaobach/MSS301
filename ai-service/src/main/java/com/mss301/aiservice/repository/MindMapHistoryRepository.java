package com.mss301.aiservice.repository;

import com.mss301.aiservice.entity.MindMapHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MindMapHistoryRepository extends JpaRepository<MindMapHistory, Long> {

    List<MindMapHistory> findByUserIdOrderByCreatedAtDesc(String userId);

    Optional<MindMapHistory> findByHistoryId(String historyId);

    List<MindMapHistory> findTop10ByUserIdOrderByCreatedAtDesc(String userId);
}