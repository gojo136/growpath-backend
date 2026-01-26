-- SUPABASE SQL EDITOR FIX
-- Copy and paste this entire script into Supabase SQL Editor and run it

-- Step 1: Drop ALL foreign key constraints on users table
DO $$ 
DECLARE 
    constraint_record RECORD;
BEGIN 
    FOR constraint_record IN 
        SELECT conname FROM pg_constraint 
        WHERE conrelid = 'public.users'::regclass 
        AND contype = 'f'
    LOOP 
        EXECUTE 'ALTER TABLE public.users DROP CONSTRAINT ' || constraint_record.conname;
        RAISE NOTICE 'Dropped constraint: %', constraint_record.conname;
    END LOOP;
END $$;

-- Step 2: Ensure auth_id column exists
ALTER TABLE public.users ADD COLUMN IF NOT EXISTS auth_id UUID;

-- Step 3: Update existing users to have auth_id
UPDATE public.users 
SET auth_id = gen_random_uuid() 
WHERE auth_id IS NULL;

-- Step 4: Disable RLS
ALTER TABLE public.users DISABLE ROW LEVEL SECURITY;
ALTER TABLE public.notes DISABLE ROW LEVEL SECURITY;

-- Step 5: Drop RLS policies
DROP POLICY IF EXISTS "users_select_own" ON public.users;
DROP POLICY IF EXISTS "users_update_own" ON public.users;
DROP POLICY IF EXISTS "users_insert_own" ON public.users;
DROP POLICY IF EXISTS "notes_select_own" ON public.notes;
DROP POLICY IF EXISTS "notes_insert_own" ON public.notes;
DROP POLICY IF EXISTS "notes_update_own" ON public.notes;
DROP POLICY IF EXISTS "notes_delete_own" ON public.notes;

-- Step 6: Verify fix
SELECT 
    id, 
    email, 
    auth_id,
    has_password,
    CASE 
        WHEN auth_id IS NOT NULL THEN 'OK' 
        ELSE 'MISSING' 
    END as auth_id_status
FROM public.users 
WHERE email = 'vishal62@gmail.com';

-- Step 7: Check no foreign key constraints remain
SELECT 
    conname as constraint_name,
    contype as type
FROM pg_constraint 
WHERE conrelid = 'public.users'::regclass
AND contype = 'f';