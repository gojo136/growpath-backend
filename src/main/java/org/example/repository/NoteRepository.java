package org.example.repository;

import org.example.entity.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    // Find all active notes for a user (not archived) with pagination
    // Ordered by: pinned notes first, then by updated date (newest first)
    @Query("SELECT n FROM Note n WHERE n.userId = :userId AND n.isArchived = false " +
           "ORDER BY n.isPinned DESC, n.updatedAt DESC")
    Page<Note> findActiveNotesByUserId(@Param("userId") Long userId, Pageable pageable);

    // Find a specific note by ID and user ID (security check)
    @Query("SELECT n FROM Note n WHERE n.id = :noteId AND n.userId = :userId")
    Optional<Note> findByIdAndUserId(@Param("noteId") Long noteId, @Param("userId") Long userId);

    // Search notes by title or content (case-insensitive)
    @Query("SELECT n FROM Note n WHERE n.userId = :userId AND n.isArchived = false " +
           "AND (LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(n.content) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY n.isPinned DESC, n.updatedAt DESC")
    Page<Note> searchNotesByUserId(@Param("userId") Long userId, 
                                   @Param("searchTerm") String searchTerm, 
                                   Pageable pageable);

    // Find notes by category for a user
    @Query("SELECT n FROM Note n WHERE n.userId = :userId AND n.isArchived = false " +
           "AND n.category = :category " +
           "ORDER BY n.isPinned DESC, n.updatedAt DESC")
    Page<Note> findNotesByUserIdAndCategory(@Param("userId") Long userId, 
                                            @Param("category") String category, 
                                            Pageable pageable);

    // Count active notes for a user
    @Query("SELECT COUNT(n) FROM Note n WHERE n.userId = :userId AND n.isArchived = false")
    Long countActiveNotesByUserId(@Param("userId") Long userId);

    // Check if note exists and belongs to user
    boolean existsByIdAndUserId(Long id, Long userId);
}