package org.example.repository;

import org.example.entity.VoiceNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VoiceNoteRepository extends JpaRepository<VoiceNote, Long> {
    List<VoiceNote> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<VoiceNote> findByUserIdAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(Long userId, String title);
}
