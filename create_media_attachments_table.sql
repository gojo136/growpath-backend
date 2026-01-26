-- Create media_attachments table for file uploads
CREATE TABLE IF NOT EXISTS media_attachments (
    id BIGSERIAL PRIMARY KEY,
    note_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(20) NOT NULL CHECK (file_type IN ('IMAGE', 'VOICE')),
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    mime_type VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_media_note_id ON media_attachments(note_id);
CREATE INDEX IF NOT EXISTS idx_media_user_id ON media_attachments(user_id);
CREATE INDEX IF NOT EXISTS idx_media_type ON media_attachments(file_type);

-- Enable RLS (Row Level Security)
ALTER TABLE media_attachments ENABLE ROW LEVEL SECURITY;

-- Create RLS policies
DROP POLICY IF EXISTS "Users can only access their own media attachments" ON media_attachments;
CREATE POLICY "Users can only access their own media attachments" ON media_attachments
    FOR ALL USING (auth.uid()::text = user_id::text);

-- Grant permissions
GRANT ALL ON media_attachments TO postgres, anon, authenticated;
GRANT USAGE, SELECT ON SEQUENCE media_attachments_id_seq TO postgres, anon, authenticated;

-- Add foreign key constraints (optional, but recommended)
-- ALTER TABLE media_attachments ADD CONSTRAINT fk_media_note 
--     FOREIGN KEY (note_id) REFERENCES notes(id) ON DELETE CASCADE;
-- ALTER TABLE media_attachments ADD CONSTRAINT fk_media_user 
--     FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;