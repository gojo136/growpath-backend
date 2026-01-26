-- Complete Database Fix for Foreign Key Constraint Issue
-- Execute this in your PostgreSQL/Supabase database

-- Step 1: Drop problematic constraints
ALTER TABLE public.users DROP CONSTRAINT IF EXISTS users_auth_id_fkey;
ALTER TABLE public.users DROP CONSTRAINT IF EXISTS fk_users_auth_id;

-- Step 2: Ensure auth_id column exists with correct type
ALTER TABLE public.users ADD COLUMN IF NOT EXISTS auth_id UUID;

-- Step 3: Update existing users to have auth_id
UPDATE public.users 
SET auth_id = gen_random_uuid() 
WHERE auth_id IS NULL;

-- Step 4: Make auth_id NOT NULL for consistency
ALTER TABLE public.users ALTER COLUMN auth_id SET NOT NULL;

-- Step 5: Add index for performance
CREATE INDEX IF NOT EXISTS idx_users_auth_id ON public.users(auth_id);

-- Step 6: Ensure has_password column exists and is properly set
ALTER TABLE public.users ADD COLUMN IF NOT EXISTS has_password BOOLEAN DEFAULT FALSE;

UPDATE public.users 
SET has_password = CASE 
    WHEN password IS NOT NULL AND password != '' THEN TRUE 
    ELSE FALSE 
END
WHERE has_password IS NULL;

-- Step 7: Add other missing columns if they don't exist
ALTER TABLE public.users ADD COLUMN IF NOT EXISTS google_id VARCHAR(100);
ALTER TABLE public.users ADD COLUMN IF NOT EXISTS photo_url VARCHAR(500);

-- Step 8: Create indexes for Google fields
CREATE INDEX IF NOT EXISTS idx_users_google_id ON public.users(google_id) WHERE google_id IS NOT NULL;

-- Step 9: Verify the fix
SELECT 
    COUNT(*) as total_users,
    COUNT(auth_id) as users_with_auth_id,
    COUNT(CASE WHEN has_password = TRUE THEN 1 END) as users_with_password
FROM public.users;

-- Step 10: Show sample data to verify
SELECT 
    id, 
    email, 
    auth_id,
    has_password,
    google_id IS NOT NULL as has_google_id
FROM public.users 
LIMIT 5;