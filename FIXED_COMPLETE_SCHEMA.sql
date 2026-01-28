-- COMPREHENSIVE FIXED DATABASE SCHEMA FOR SELFGROWTH APP
-- This script fixes all identified issues and creates a complete, consistent database structure

-- =====================================================
-- STEP 1: CLEAN UP EXISTING PROBLEMATIC CONSTRAINTS
-- =====================================================

-- Drop all problematic foreign key constraints on users table
DO $$ 
DECLARE 
    constraint_record RECORD;
BEGIN 
    FOR constraint_record IN 
        SELECT conname FROM pg_constraint 
        WHERE conrelid = 'public.users'::regclass 
        AND contype = 'f'
    LOOP 
        EXECUTE 'ALTER TABLE public.users DROP CONSTRAINT IF EXISTS ' || constraint_record.conname;
        RAISE NOTICE 'Dropped constraint: %', constraint_record.conname;
    END LOOP;
END $$;

-- Disable RLS temporarily for setup
ALTER TABLE IF EXISTS public.users DISABLE ROW LEVEL SECURITY;
ALTER TABLE IF EXISTS public.notes DISABLE ROW LEVEL SECURITY;
ALTER TABLE IF EXISTS public.media_attachments DISABLE ROW LEVEL SECURITY;
ALTER TABLE IF EXISTS public.checklist_items DISABLE ROW LEVEL SECURITY;
ALTER TABLE IF EXISTS public.note_formatting DISABLE ROW LEVEL SECURITY;

-- =====================================================
-- STEP 2: CREATE/UPDATE USERS TABLE
-- =====================================================

CREATE TABLE IF NOT EXISTS public.users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255), -- Nullable for Google Sign-In users
    full_name VARCHAR(150) NOT NULL,
    age INTEGER CHECK (age >= 13 AND age <= 120),
    profession VARCHAR(100),
    location VARCHAR(150),
    
    -- Google Sign-In fields
    google_id VARCHAR(100),
    photo_url VARCHAR(500),
    has_password BOOLEAN NOT NULL DEFAULT FALSE,
    
    -- Auth ID for RLS (UUID type)
    auth_id UUID NOT NULL DEFAULT gen_random_uuid(),
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Add missing columns to existing users table
ALTER TABLE public.users ADD COLUMN IF NOT EXISTS google_id VARCHAR(100);
ALTER TABLE public.users ADD COLUMN IF NOT EXISTS photo_url VARCHAR(500);
ALTER TABLE public.users ADD COLUMN IF NOT EXISTS has_password BOOLEAN DEFAULT FALSE;
ALTER TABLE public.users ADD COLUMN IF NOT EXISTS auth_id UUID DEFAULT gen_random_uuid();

-- Update existing data
UPDATE public.users 
SET has_password = CASE 
    WHEN password IS NOT NULL AND password != '' THEN TRUE 
    ELSE FALSE 
END
WHERE has_password IS NULL;

UPDATE public.users 
SET auth_id = gen_random_uuid() 
WHERE auth_id IS NULL;

-- Make auth_id NOT NULL
ALTER TABLE public.users ALTER COLUMN auth_id SET NOT NULL;

-- Create unique constraints and indexes
ALTER TABLE public.users ADD CONSTRAINT IF NOT EXISTS uk_users_email UNIQUE (email);
ALTER TABLE public.users ADD CONSTRAINT IF NOT EXISTS uk_users_username UNIQUE (username);

CREATE INDEX IF NOT EXISTS idx_users_email ON public.users(email);
CREATE INDEX IF NOT EXISTS idx_users_username ON public.users(username);
CREATE INDEX IF NOT EXISTS idx_users_google_id ON public.users(google_id) WHERE google_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_users_auth_id ON public.users(auth_id);

-- =====================================================
-- STEP 3: CREATE/UPDATE NOTES TABLE
-- =====================================================

CREATE TABLE IF NOT EXISTS public.notes (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200),
    content TEXT NOT NULL,
    formatted_content TEXT,
    category VARCHAR(50),
    
    -- Note type and features
    note_type VARCHAR(20) DEFAULT 'TEXT' CHECK (note_type IN ('TEXT', 'CHECKLIST', 'MIXED')),
    has_images BOOLEAN DEFAULT FALSE,
    has_voice_notes BOOLEAN DEFAULT FALSE,
    has_checklist BOOLEAN DEFAULT FALSE,
    has_formatting BOOLEAN DEFAULT FALSE,
    
    -- JSON data for checkboxes
    checkbox_data JSONB,
    
    -- Status flags
    is_pinned BOOLEAN DEFAULT FALSE,
    is_archived BOOLEAN DEFAULT FALSE,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key to users
    CONSTRAINT fk_notes_user FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE
);

-- Add missing columns to existing notes table
ALTER TABLE public.notes ADD COLUMN IF NOT EXISTS formatted_content TEXT;
ALTER TABLE public.notes ADD COLUMN IF NOT EXISTS note_type VARCHAR(20) DEFAULT 'TEXT';
ALTER TABLE public.notes ADD COLUMN IF NOT EXISTS has_images BOOLEAN DEFAULT FALSE;
ALTER TABLE public.notes ADD COLUMN IF NOT EXISTS has_voice_notes BOOLEAN DEFAULT FALSE;
ALTER TABLE public.notes ADD COLUMN IF NOT EXISTS has_checklist BOOLEAN DEFAULT FALSE;
ALTER TABLE public.notes ADD COLUMN IF NOT EXISTS has_formatting BOOLEAN DEFAULT FALSE;
ALTER TABLE public.notes ADD COLUMN IF NOT EXISTS checkbox_data JSONB;

-- Create indexes for notes
CREATE INDEX IF NOT EXISTS idx_notes_user_id ON public.notes(user_id);
CREATE INDEX IF NOT EXISTS idx_notes_archived ON public.notes(is_archived);
CREATE INDEX IF NOT EXISTS idx_notes_pinned ON public.notes(is_pinned);
CREATE INDEX IF NOT EXISTS idx_notes_type ON public.notes(note_type);
CREATE INDEX IF NOT EXISTS idx_notes_features ON public.notes(has_images, has_voice_notes, has_checklist);
CREATE INDEX IF NOT EXISTS idx_notes_checkbox_data ON public.notes USING GIN (checkbox_data);
CREATE INDEX IF NOT EXISTS idx_notes_formatted_content ON public.notes USING GIN (to_tsvector('english', formatted_content));

-- =====================================================
-- STEP 4: CREATE MEDIA ATTACHMENTS TABLE
-- =====================================================

CREATE TABLE IF NOT EXISTS public.media_attachments (
    id BIGSERIAL PRIMARY KEY,
    note_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(255),
    file_url VARCHAR(500) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_type VARCHAR(20) NOT NULL CHECK (file_type IN ('IMAGE', 'VOICE')),
    file_size BIGINT,
    mime_type VARCHAR(100),
    duration_seconds INTEGER, -- For voice notes
    uploaded_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign keys
    CONSTRAINT fk_media_note FOREIGN KEY (note_id) REFERENCES public.notes(id) ON DELETE CASCADE,
    CONSTRAINT fk_media_user FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE
);

-- Create indexes for media attachments
CREATE INDEX IF NOT EXISTS idx_media_note_id ON public.media_attachments(note_id);
CREATE INDEX IF NOT EXISTS idx_media_user_id ON public.media_attachments(user_id);
CREATE INDEX IF NOT EXISTS idx_media_type ON public.media_attachments(file_type);

-- =====================================================
-- STEP 5: CREATE CHECKLIST ITEMS TABLE
-- =====================================================

CREATE TABLE IF NOT EXISTS public.checklist_items (
    id BIGSERIAL PRIMARY KEY,
    note_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    item_text TEXT NOT NULL,
    is_checked BOOLEAN DEFAULT FALSE,
    item_order INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign keys
    CONSTRAINT fk_checklist_note FOREIGN KEY (note_id) REFERENCES public.notes(id) ON DELETE CASCADE,
    CONSTRAINT fk_checklist_user FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE
);

-- Create indexes for checklist items
CREATE INDEX IF NOT EXISTS idx_checklist_note_id ON public.checklist_items(note_id);
CREATE INDEX IF NOT EXISTS idx_checklist_user_id ON public.checklist_items(user_id);
CREATE INDEX IF NOT EXISTS idx_checklist_order ON public.checklist_items(note_id, item_order);

-- =====================================================
-- STEP 6: CREATE NOTE FORMATTING TABLE
-- =====================================================

CREATE TABLE IF NOT EXISTS public.note_formatting (
    id BIGSERIAL PRIMARY KEY,
    note_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    start_position INTEGER NOT NULL,
    end_position INTEGER NOT NULL,
    format_type VARCHAR(20) NOT NULL CHECK (format_type IN ('BOLD', 'ITALIC', 'UNDERLINE', 'HIGHLIGHT')),
    format_value VARCHAR(100), -- For colors, etc.
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign keys
    CONSTRAINT fk_formatting_note FOREIGN KEY (note_id) REFERENCES public.notes(id) ON DELETE CASCADE,
    CONSTRAINT fk_formatting_user FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE
);

-- Create indexes for note formatting
CREATE INDEX IF NOT EXISTS idx_formatting_note_id ON public.note_formatting(note_id);
CREATE INDEX IF NOT EXISTS idx_formatting_user_id ON public.note_formatting(user_id);

-- =====================================================
-- STEP 7: CREATE UPDATE TRIGGERS
-- =====================================================

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for all tables
DROP TRIGGER IF EXISTS update_users_updated_at ON public.users;
CREATE TRIGGER update_users_updated_at 
    BEFORE UPDATE ON public.users 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_notes_updated_at ON public.notes;
CREATE TRIGGER update_notes_updated_at 
    BEFORE UPDATE ON public.notes 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_media_updated_at ON public.media_attachments;
CREATE TRIGGER update_media_updated_at 
    BEFORE UPDATE ON public.media_attachments 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_checklist_updated_at ON public.checklist_items;
CREATE TRIGGER update_checklist_updated_at 
    BEFORE UPDATE ON public.checklist_items 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- STEP 8: SETUP ROW LEVEL SECURITY (RLS)
-- =====================================================

-- Enable RLS on all tables
ALTER TABLE public.users ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.notes ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.media_attachments ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.checklist_items ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.note_formatting ENABLE ROW LEVEL SECURITY;

-- Drop existing policies
DROP POLICY IF EXISTS "users_policy" ON public.users;
DROP POLICY IF EXISTS "notes_policy" ON public.notes;
DROP POLICY IF EXISTS "media_attachments_policy" ON public.media_attachments;
DROP POLICY IF EXISTS "checklist_items_policy" ON public.checklist_items;
DROP POLICY IF EXISTS "note_formatting_policy" ON public.note_formatting;

-- Create RLS policies
CREATE POLICY "users_policy" ON public.users
    FOR ALL USING (auth.uid() = auth_id);

CREATE POLICY "notes_policy" ON public.notes
    FOR ALL USING (auth.uid()::text = (SELECT auth_id::text FROM public.users WHERE id = notes.user_id));

CREATE POLICY "media_attachments_policy" ON public.media_attachments
    FOR ALL USING (auth.uid()::text = (SELECT auth_id::text FROM public.users WHERE id = media_attachments.user_id));

CREATE POLICY "checklist_items_policy" ON public.checklist_items
    FOR ALL USING (auth.uid()::text = (SELECT auth_id::text FROM public.users WHERE id = checklist_items.user_id));

CREATE POLICY "note_formatting_policy" ON public.note_formatting
    FOR ALL USING (auth.uid()::text = (SELECT auth_id::text FROM public.users WHERE id = note_formatting.user_id));

-- =====================================================
-- STEP 9: GRANT PERMISSIONS
-- =====================================================

-- Grant permissions to authenticated users
GRANT ALL ON public.users TO postgres, anon, authenticated;
GRANT ALL ON public.notes TO postgres, anon, authenticated;
GRANT ALL ON public.media_attachments TO postgres, anon, authenticated;
GRANT ALL ON public.checklist_items TO postgres, anon, authenticated;
GRANT ALL ON public.note_formatting TO postgres, anon, authenticated;

-- Grant sequence permissions
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO postgres, anon, authenticated;

-- =====================================================
-- STEP 10: VERIFICATION QUERIES
-- =====================================================

-- Verify table structures
SELECT 
    schemaname,
    tablename,
    tableowner,
    rowsecurity
FROM pg_tables 
WHERE schemaname = 'public' 
AND tablename IN ('users', 'notes', 'media_attachments', 'checklist_items', 'note_formatting')
ORDER BY tablename;

-- Verify users table structure
SELECT 
    column_name, 
    data_type, 
    is_nullable, 
    column_default 
FROM information_schema.columns 
WHERE table_name = 'users' 
AND table_schema = 'public'
ORDER BY ordinal_position;

-- Check for any remaining problematic constraints
SELECT 
    conname as constraint_name,
    contype as constraint_type,
    pg_get_constraintdef(oid) as constraint_definition
FROM pg_constraint 
WHERE conrelid = 'public.users'::regclass
AND conname LIKE '%auth_id%';

-- Verify sample data
SELECT 
    COUNT(*) as total_users,
    COUNT(auth_id) as users_with_auth_id,
    COUNT(CASE WHEN has_password = TRUE THEN 1 END) as users_with_password,
    COUNT(google_id) as google_users
FROM public.users;

COMMIT;

-- Success message
SELECT 'Database schema has been successfully fixed and optimized!' as status;