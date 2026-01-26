-- URGENT FIX: Drop the foreign key constraint that's causing the error
-- Run this FIRST before anything else

-- Drop the problematic foreign key constraint
ALTER TABLE public.users DROP CONSTRAINT IF EXISTS users_auth_id_fkey;

-- Check if there are any other auth_id related constraints
SELECT 
    conname as constraint_name,
    contype as constraint_type,
    pg_get_constraintdef(oid) as constraint_definition
FROM pg_constraint 
WHERE conrelid = 'public.users'::regclass
AND conname LIKE '%auth_id%';

-- If the above query shows any constraints, drop them:
-- ALTER TABLE public.users DROP CONSTRAINT constraint_name_here;

-- Verify no foreign key constraints exist on auth_id
\d public.users