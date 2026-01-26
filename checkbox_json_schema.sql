-- JSON-based Checkbox Implementation
-- Add checkbox_data column to existing notes table

ALTER TABLE notes ADD COLUMN IF NOT EXISTS checkbox_data JSONB;

-- Create index for checkbox queries
CREATE INDEX IF NOT EXISTS idx_notes_checkbox_data ON notes USING GIN (checkbox_data);

-- Example checkbox_data structure:
-- {
--   "items": [
--     {"id": "uuid1", "text": "Task 1", "checked": true, "position": 0},
--     {"id": "uuid2", "text": "Task 2", "checked": false, "position": 1}
--   ]
-- }

-- Query examples:
-- Get notes with uncompleted checkboxes
SELECT * FROM notes 
WHERE checkbox_data->'items' @> '[{"checked": false}]';

-- Count completed vs total checkboxes
SELECT 
  id, title,
  jsonb_array_length(checkbox_data->'items') as total_items,
  (SELECT COUNT(*) FROM jsonb_array_elements(checkbox_data->'items') item WHERE item->>'checked' = 'true') as completed_items
FROM notes 
WHERE checkbox_data IS NOT NULL;