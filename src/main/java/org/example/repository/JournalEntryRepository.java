package org.example.repository;

import org.example.entity.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {
    List<JournalEntry> findByUserIdOrderByDateDescCreatedAtDesc(Long userId);
    List<JournalEntry> findByUserIdAndTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrderByDateDesc(
            Long userId, String titleQuery, String contentQuery);
}