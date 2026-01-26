package org.example.repository;

import org.example.entity.ChecklistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChecklistItemRepository extends JpaRepository<ChecklistItem, Long> {

    List<ChecklistItem> findByNoteIdAndUserIdOrderByItemOrderAsc(Long noteId, Long userId);

    List<ChecklistItem> findByNoteIdOrderByItemOrderAsc(Long noteId);

    Optional<ChecklistItem> findByIdAndUserId(Long id, Long userId);

    void deleteByNoteIdAndUserId(Long noteId, Long userId);

    @Query("SELECT COUNT(c) FROM ChecklistItem c WHERE c.noteId = :noteId AND c.userId = :userId")
    Long countByNoteIdAndUserId(@Param("noteId") Long noteId, @Param("userId") Long userId);

    @Query("SELECT COUNT(c) FROM ChecklistItem c WHERE c.noteId = :noteId AND c.userId = :userId AND c.isChecked = true")
    Long countCompletedByNoteIdAndUserId(@Param("noteId") Long noteId, @Param("userId") Long userId);

    @Query("SELECT MAX(c.itemOrder) FROM ChecklistItem c WHERE c.noteId = :noteId AND c.userId = :userId")
    Integer findMaxOrderByNoteIdAndUserId(@Param("noteId") Long noteId, @Param("userId") Long userId);
}