package org.example.expert.domain.manager.repository;

import org.example.expert.domain.manager.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long> {
}
