package org.example.repository;

import org.example.entity.MediaAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MediaAttachmentRepository extends JpaRepository<MediaAttachment, Long> {

    /**
     * Find all media attachments for a specific note
     */
    List<MediaAttachment> findByNoteId(Long noteId);

    /**
     * Find all media attachments for a specific user
     */
    List<MediaAttachment> findByUserId(Long userId);

    /**
     * Find media attachment by ID and user ID (for security)
     */
    Optional<MediaAttachment> findByIdAndUserId(Long id, Long userId);

    /**
     * Find media attachments by note ID and user ID
     */
    List<MediaAttachment> findByNoteIdAndUserId(Long noteId, Long userId);

    /**
     * Delete all media attachments for a note
     */
    void deleteByNoteId(Long noteId);

    /**
     * Count media attachments for a note
     */
    long countByNoteId(Long noteId);
}
