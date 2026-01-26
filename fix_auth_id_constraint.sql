-- Fix auth_id foreign key constraint issue
-- This script removes the problematic foreign key constraint and sets up proper auth_id handling

-- Step 1: Drop the problematic foreign key constraint if it exists
ALTER TABLE public.users DROP CONSTRAINT IF EXISTS users_auth_id_fkey;

-- Step 2: Ensure auth_id column exists and is properly typed
ALTER TABLE public.users 
ADD COLUMN IF NOT EXISTS auth_id UUID;

-- Step 3: Update existing users to have auth_id if they don't have one
UPDATE public.users 
SET auth_id = gen_random_uuid() 
WHERE auth_id IS NULL;

-- Step 4: Create index for better performance (without foreign key constraint)
CREATE INDEX IF NOT EXISTS idx_users_auth_id ON public.users(auth_id);

-- Step 5: Make auth_id NOT NULL for future records
ALTER TABLE public.users ALTER COLUMN auth_id SET NOT NULL;

-- Step 6: Verify the fix
SELECT 
    id, 
    email, 
    auth_id,
    CASE 
        WHEN auth_id IS NOT NULL THEN 'OK' 
        ELSE 'MISSING' 
    END as auth_id_status
FROM public.users 
LIMIT 10;

-- Step 7: Check if there are any remaining constraint issues
SELECT 
    conname as constraint_name,
    contype as constraint_type
FROM pg_constraint 
WHERE conrelid = 'public.users'::regclass
AND conname LIKE '%auth_id%';