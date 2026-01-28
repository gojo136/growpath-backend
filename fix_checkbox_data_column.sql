-- Fix checkbox_data column type mismatch
-- Change from JSONB to TEXT to match Hibernate mapping

ALTER TABLE notes ALTER COLUMN checkbox_data TYPE TEXT;

-- Update any existing JSONB data to TEXT (if any exists)
UPDATE notes SET checkbox_data = checkbox_data::text WHERE checkbox_data IS NOT NULL;