-- Add Google Sign-In fields to users table
ALTER TABLE users 
ADD COLUMN google_id VARCHAR(100),
ADD COLUMN photo_url VARCHAR(500),
ADD COLUMN has_password BOOLEAN NOT NULL DEFAULT FALSE;

-- Update existing users to have has_password = true if they have a password
UPDATE users 
SET has_password = TRUE 
WHERE password IS NOT NULL;

-- Create index for google_id for faster lookups
CREATE INDEX idx_users_google_id ON users(google_id);