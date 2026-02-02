package org.example.service;

import org.example.dto.request.CreateJournalEntryRequest;
import org.example.dto.response.JournalEntryResponse;
import org.example.entity.JournalEntry;
import org.example.repository.JournalEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository repository;

    @Autowired
    private SupabaseStorageService storageService;

    public JournalEntryResponse createJournalEntry(Long userId, CreateJournalEntryRequest request, 
                                                  List<MultipartFile> photos, MultipartFile voiceNote) {
        JournalEntry entry = new JournalEntry();
        entry.setUserId(userId);
        entry.setTitle(request.getTitle());
        entry.setContent(request.getContent());
        entry.setDate(request.getDate());
        entry.setMood(request.getMood());
        entry.setLocation(request.getLocation());

        // Handle photo uploads (max 5)
        if (photos != null && !photos.isEmpty()) {
            List<String> photoUrls = photos.stream()
                .limit(5)
                .map(photo -> {
                    try {
                        return storageService.uploadFile(photo, "journal-photos", userId);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to upload photo: " + e.getMessage());
                    }
                })
                .collect(Collectors.toList());
            entry.setPhotos(photoUrls);
        }

        // Handle voice note upload
        if (voiceNote != null && !voiceNote.isEmpty()) {
            try {
                String voiceUrl = storageService.uploadFile(voiceNote, "journal-voice", userId);
                entry.setVoiceNoteUrl(voiceUrl);
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload voice note: " + e.getMessage());
            }
        }

        JournalEntry saved = repository.save(entry);
        return new JournalEntryResponse(saved);
    }

    public List<JournalEntryResponse> getUserJournalEntries(Long userId) {
        return repository.findByUserIdOrderByDateDescCreatedAtDesc(userId)
                .stream()
                .map(JournalEntryResponse::new)
                .collect(Collectors.toList());
    }

    public JournalEntryResponse getJournalEntryById(Long id, Long userId) {
        JournalEntry entry = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Journal entry not found"));
        
        if (!entry.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }
        
        return new JournalEntryResponse(entry);
    }

    public JournalEntryResponse updateJournalEntry(Long id, Long userId, CreateJournalEntryRequest request,
                                                  List<MultipartFile> photos, MultipartFile voiceNote) {
        JournalEntry entry = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Journal entry not found"));
        
        if (!entry.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        entry.setTitle(request.getTitle());
        entry.setContent(request.getContent());
        entry.setDate(request.getDate());
        entry.setMood(request.getMood());
        entry.setLocation(request.getLocation());

        // Handle new photos if provided
        if (photos != null && !photos.isEmpty()) {
            // Delete old photos
            if (entry.getPhotos() != null) {
                entry.getPhotos().forEach(storageService::deleteFile);
            }
            
            List<String> photoUrls = photos.stream()
                .limit(5)
                .map(photo -> {
                    try {
                        return storageService.uploadFile(photo, "journal-photos", userId);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to upload photo: " + e.getMessage());
                    }
                })
                .collect(Collectors.toList());
            entry.setPhotos(photoUrls);
        }

        // Handle new voice note if provided
        if (voiceNote != null && !voiceNote.isEmpty()) {
            // Delete old voice note
            if (entry.getVoiceNoteUrl() != null) {
                storageService.deleteFile(entry.getVoiceNoteUrl());
            }
            
            try {
                String voiceUrl = storageService.uploadFile(voiceNote, "journal-voice", userId);
                entry.setVoiceNoteUrl(voiceUrl);
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload voice note: " + e.getMessage());
            }
        }

        JournalEntry saved = repository.save(entry);
        return new JournalEntryResponse(saved);
    }

    public void deleteJournalEntry(Long id, Long userId) {
        JournalEntry entry = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Journal entry not found"));
        
        if (!entry.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }
        
        // Delete associated files from storage
        if (entry.getPhotos() != null) {
            entry.getPhotos().forEach(storageService::deleteFile);
        }
        if (entry.getVoiceNoteUrl() != null) {
            storageService.deleteFile(entry.getVoiceNoteUrl());
        }
        
        repository.delete(entry);
    }

    public List<JournalEntryResponse> searchJournalEntries(Long userId, String query) {
        return repository.findByUserIdAndTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrderByDateDesc(
                userId, query, query)
                .stream()
                .map(JournalEntryResponse::new)
                .collect(Collectors.toList());
    }
}