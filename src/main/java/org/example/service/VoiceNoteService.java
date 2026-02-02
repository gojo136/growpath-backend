package org.example.service;

import org.example.dto.response.VoiceNoteResponse;
import org.example.entity.VoiceNote;
import org.example.repository.VoiceNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VoiceNoteService {

    @Autowired
    private VoiceNoteRepository voiceNoteRepository;

    @Autowired
    private SupabaseStorageService storageService;

    public VoiceNoteResponse createVoiceNote(Long userId, String title, MultipartFile file, Long durationMs, String category) throws IOException {
        String filePath = storageService.saveVoiceNoteFile(file, userId);
        
        VoiceNote voiceNote = new VoiceNote(userId, title, filePath, durationMs, category);
        VoiceNote savedNote = voiceNoteRepository.save(voiceNote);
        
        return mapToResponse(savedNote);
    }

    public List<VoiceNoteResponse> getVoiceNotesByUserId(Long userId) {
        return voiceNoteRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<VoiceNoteResponse> searchVoiceNotes(Long userId, String query) {
        return voiceNoteRepository.findByUserIdAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(userId, query)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public VoiceNoteResponse updateVoiceNote(Long id, Long userId, String newTitle) {
        System.out.println("Service: Updating voice note ID " + id + " for user " + userId + " with title: " + newTitle);
        
        try {
            VoiceNote voiceNote = voiceNoteRepository.findById(id)
                    .orElseThrow(() -> {
                        System.err.println("Voice note not found with ID: " + id);
                        return new RuntimeException("Voice note not found");
                    });
            
            System.out.println("Found voice note: " + voiceNote.getTitle() + " owned by user: " + voiceNote.getUserId());
            
            if (!voiceNote.getUserId().equals(userId)) {
                System.err.println("Unauthorized access attempt. Voice note owner: " + voiceNote.getUserId() + ", requesting user: " + userId);
                throw new RuntimeException("Unauthorized");
            }
            
            System.out.println("Updating title from '" + voiceNote.getTitle() + "' to '" + newTitle + "'");
            voiceNote.setTitle(newTitle);
            
            VoiceNote updatedNote = voiceNoteRepository.save(voiceNote);
            System.out.println("Successfully updated voice note. New title: " + updatedNote.getTitle());
            
            return mapToResponse(updatedNote);
        } catch (Exception e) {
            System.err.println("Error in updateVoiceNote service: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public void deleteVoiceNote(Long id, Long userId) {
        VoiceNote voiceNote = voiceNoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voice note not found"));
        
        if (!voiceNote.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        storageService.deleteFile(voiceNote.getFilePath());
        voiceNoteRepository.delete(voiceNote);
    }

    private VoiceNoteResponse mapToResponse(VoiceNote voiceNote) {
        return new VoiceNoteResponse(
                voiceNote.getId(),
                voiceNote.getTitle(),
                voiceNote.getFilePath(),
                voiceNote.getDurationMs(),
                voiceNote.getCategory(),
                voiceNote.getCreatedAt()
        );
    }
}
