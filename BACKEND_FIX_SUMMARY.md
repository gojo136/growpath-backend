# Backend Fix: Missing uploadFile Method

## Problem
The backend was failing to compile with the following errors:
- `cannot find symbol: method uploadFile(org.springframework.web.multipart.MultipartFile,java.lang.String)`
- `incompatible types: inference variable T has incompatible bounds`

## Root Cause
The `JournalEntryService` was calling `storageService.uploadFile(photo, "journal-photos")` and `storageService.uploadFile(voiceNote, "journal-voice")`, but the `SupabaseStorageService` class didn't have an `uploadFile` method with this signature.

## Solution
Added the missing `uploadFile` method to `SupabaseStorageService.java`:

```java
public String uploadFile(MultipartFile file, String bucket) throws IOException {
    if (bucket.equals("journal-photos")) {
        validateImageFile(file);
        return uploadToSupabase(file, storageConfig.getImagesBucket(), 
            generateFileName("journal", 0L, 0L, getFileExtension(file.getOriginalFilename())));
    } else if (bucket.equals("journal-voice")) {
        validateAudioFile(file);
        return uploadToSupabase(file, storageConfig.getAudioBucket(), 
            generateFileName("journal_voice", 0L, 0L, getFileExtension(file.getOriginalFilename())));
    } else {
        throw new IOException("Unsupported bucket: " + bucket);
    }
}
```

## Key Features of the Fix
1. **Proper File Validation**: Calls `validateImageFile()` for photos and `validateAudioFile()` for voice notes
2. **Bucket Routing**: Routes journal photos to image bucket and voice notes to audio bucket
3. **Unique File Names**: Generates unique filenames using the existing `generateFileName()` method
4. **Error Handling**: Throws IOException for unsupported bucket types
5. **Improved Error Messages**: Enhanced error messages in JournalEntryService to include actual exception details

## Files Modified
1. **SupabaseStorageService.java**: Added the missing `uploadFile` method
2. **JournalEntryService.java**: Improved error messages to include exception details

## Testing
The backend should now compile successfully and handle journal entry uploads with photos and voice notes properly.

## Usage
This method is specifically used by the journal entry feature to upload:
- Photos: `storageService.uploadFile(photo, "journal-photos")`
- Voice notes: `storageService.uploadFile(voiceNote, "journal-voice")`

The method integrates with the existing Supabase storage infrastructure and follows the same patterns as other upload methods in the service.