-- COMPLETE ENHANCED NOTES DATABASE SCHEMA
-- This schema supports voice recording, image upload, text formatting, and checkboxes

-- 1. Update existing notes table with enhanced features
ALTER TABLE notes ADD COLUMN IF NOT EXISTS formatted_content TEXT;
ALTER TABLE notes ADD COLUMN IF NOT EXISTS note_type VARCHAR(20) DEFAULT 'TEXT';
ALTER TABLE notes ADD COLUMN IF NOT EXISTS has_images BOOLEAN DEFAULT FALSE;
ALTER TABLE notes ADD COLUMN IF NOT EXISTS has_voice_notes BOOLEAN DEFAULT FALSE;
ALTER TABLE notes ADD COLUMN IF NOT EXISTS has_checklist BOOLEAN DEFAULT FALSE;
ALTER TABLE notes ADD COLUMN IF NOT EXISTS has_formatting BOOLEAN DEFAULT FALSE;

-- 2. Create media_attachments table (already exists but ensure correct structure)
CREATE TABLE IF NOT EXISTS media_attachments (
    id BIGSERIAL PRIMARY KEY,
    note_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(255),
    file_url VARCHAR(500) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_type VARCHAR(20) NOT NULL, -- 'IMAGE', 'VOICE'
    file_size BIGINT,
    mime_type VARCHAR(100),
    duration_seconds INTEGER, -- For voice notes
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (note_id) REFERENCES notes(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. Create checklist_items table
CREATE TABLE IF NOT EXISTS checklist_items (
    id BIGSERIAL PRIMARY KEY,
    note_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    item_text TEXT NOT NULL,
    is_checked BOOLEAN DEFAULT FALSE,
    item_order INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (note_id) REFERENCES notes(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 4. Create note_formatting table for rich text
CREATE TABLE IF NOT EXISTS note_formatting (
    id BIGSERIAL PRIMARY KEY,
    note_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    start_position INTEGER NOT NULL,
    end_position INTEGER NOT NULL,
    format_type VARCHAR(20) NOT NULL, -- 'BOLD', 'ITALIC', 'UNDERLINE', 'HIGHLIGHT'
    format_value VARCHAR(100), -- For colors, etc.
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (note_id) REFERENCES notes(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 5. Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_media_note_id ON media_attachments(note_id);
CREATE INDEX IF NOT EXISTS idx_media_user_id ON media_attachments(user_id);
CREATE INDEX IF NOT EXISTS idx_media_type ON media_attachments(file_type);
CREATE INDEX IF NOT EXISTS idx_checklist_note_id ON checklist_items(note_id);
CREATE INDEX IF NOT EXISTS idx_checklist_user_id ON checklist_items(user_id);
CREATE INDEX IF NOT EXISTS idx_checklist_order ON checklist_items(note_id, item_order);
CREATE INDEX IF NOT EXISTS idx_formatting_note_id ON note_formatting(note_id);
CREATE INDEX IF NOT EXISTS idx_notes_type ON notes(note_type);
CREATE INDEX IF NOT EXISTS idx_notes_features ON notes(has_images, has_voice_notes, has_checklist);

-- 6. Row Level Security (RLS) policies
ALTER TABLE media_attachments ENABLE ROW LEVEL SECURITY;
ALTER TABLE checklist_items ENABLE ROW LEVEL SECURITY;
ALTER TABLE note_formatting ENABLE ROW LEVEL SECURITY;

-- Media attachments policies
CREATE POLICY media_attachments_policy ON media_attachments
    FOR ALL USING (auth.uid()::text = user_id::text);

-- Checklist items policies  
CREATE POLICY checklist_items_policy ON checklist_items
    FOR ALL USING (auth.uid()::text = user_id::text);

-- Note formatting policies
CREATE POLICY note_formatting_policy ON note_formatting
    FOR ALL USING (auth.uid()::text = user_id::text);