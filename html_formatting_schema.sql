-- HTML-based Text Formatting Implementation
-- Use existing formatted_content column in notes table

-- Ensure formatted_content column exists
ALTER TABLE notes ADD COLUMN IF NOT EXISTS formatted_content TEXT;

-- Create index for formatted content searches
CREATE INDEX IF NOT EXISTS idx_notes_formatted_content ON notes USING GIN (to_tsvector('english', formatted_content));

-- Example formatted_content:
-- '<p>This is <b>bold</b> and <i>italic</i> and <u>underlined</u> text with <span style="background-color: yellow;">highlight</span></p>'

-- Query examples:
-- Search in formatted content
SELECT * FROM notes 
WHERE formatted_content ILIKE '%<b>%' 
   OR formatted_content ILIKE '%<i>%';

-- Get notes with specific formatting
SELECT id, title, 
       CASE 
         WHEN formatted_content LIKE '%<b>%' THEN true 
         ELSE false 
       END as has_bold,
       CASE 
         WHEN formatted_content LIKE '%<i>%' THEN true 
         ELSE false 
       END as has_italic
FROM notes 
WHERE formatted_content IS NOT NULL;