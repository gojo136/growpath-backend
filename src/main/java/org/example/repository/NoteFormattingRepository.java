package org.example.repository;

import org.example.entity.NoteFormatting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteFormattingRepository extends JpaRepository<NoteFormatting, Long> {

    List<NoteFormatting> findByNoteIdAndUserIdOrderByStartPositionAsc(Long noteId, Long userId);

    List<NoteFormatting> findByNoteIdOrderByStartPositionAsc(Long noteId);

    Optional<NoteFormatting> findByIdAndUserId(Long id, Long userId);

    void deleteByNoteIdAndUserId(Long noteId, Long userId);

    @Query("SELECT COUNT(f) FROM NoteFormatting f WHERE f.noteId = :noteId AND f.userId = :userId")
    Long countByNoteIdAndUserId(@Param("noteId") Long noteId, @Param("userId") Long userId);
}