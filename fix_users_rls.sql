-- Fix existing users for RLS compatibility
-- Run this in Supabase SQL Editor

-- Update existing users to have auth_id
UPDATE public.users 
SET auth_id = gen_random_uuid() 
WHERE auth_id IS NULL;

-- Verify the update
SELECT id, email, auth_id FROM public.users LIMIT 5;

-- Test RLS is working (should show data now)
SELECT COUNT(*) as user_count FROM public.users;
SELECT COUNT(*) as notes_count FROM public.notes;