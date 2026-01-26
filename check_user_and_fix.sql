-- Check if user exists and fix constraint issue
SELECT id, email, username, auth_id, has_password 
FROM public.users 
WHERE email = 'vishal62@gmail.com';

-- Force drop the constraint (it might have a different name)
SELECT 
    'ALTER TABLE public.users DROP CONSTRAINT ' || conname || ';' as drop_command
FROM pg_constraint 
WHERE conrelid = 'public.users'::regclass 
AND contype = 'f'  -- foreign key constraints only
AND conname LIKE '%auth_id%';

-- Execute the actual drops
ALTER TABLE public.users DROP CONSTRAINT IF EXISTS users_auth_id_fkey;
ALTER TABLE public.users DROP CONSTRAINT IF EXISTS fk_users_auth_id;

-- Update existing user to have auth_id if missing
UPDATE public.users 
SET auth_id = gen_random_uuid() 
WHERE email = 'vishal62@gmail.com' AND auth_id IS NULL;

-- Verify the fix
SELECT id, email, auth_id, has_password 
FROM public.users 
WHERE email = 'vishal62@gmail.com';