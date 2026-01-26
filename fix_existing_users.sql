-- Fix existing users data
UPDATE users SET has_password = TRUE WHERE password IS NOT NULL;
UPDATE users SET has_password = FALSE WHERE password IS NULL;