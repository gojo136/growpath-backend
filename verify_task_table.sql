-- Task Table Verification Script
-- Run this in your Supabase SQL editor to verify the task table structure

-- 1. Check if tasks table exists and show its structure
SELECT 
    column_name,
    data_type,
    is_nullable,
    column_default,
    character_maximum_length
FROM information_schema.columns 
WHERE table_name = 'tasks' 
ORDER BY ordinal_position;

-- 2. Verify table constraints
SELECT 
    tc.constraint_name,
    tc.constraint_type,
    kcu.column_name,
    cc.check_clause
FROM information_schema.table_constraints tc
LEFT JOIN information_schema.key_column_usage kcu 
    ON tc.constraint_name = kcu.constraint_name
LEFT JOIN information_schema.check_constraints cc 
    ON tc.constraint_name = cc.constraint_name
WHERE tc.table_name = 'tasks';

-- 3. Check indexes on tasks table
SELECT 
    indexname,
    indexdef
FROM pg_indexes 
WHERE tablename = 'tasks';

-- 4. Verify RLS policies
SELECT 
    schemaname,
    tablename,
    policyname,
    permissive,
    roles,
    cmd,
    qual,
    with_check
FROM pg_policies 
WHERE tablename = 'tasks';

-- 5. Sample data check (if any tasks exist)
SELECT 
    id,
    user_id,
    title,
    priority,
    due_date,
    due_time,
    repeat_type,
    status,
    created_at,
    updated_at
FROM tasks 
LIMIT 5;

-- 6. Count total tasks
SELECT COUNT(*) as total_tasks FROM tasks;

-- 7. Check for any tasks with serialization issues (null dates where they shouldn't be)
SELECT 
    id,
    title,
    due_date,
    due_time,
    created_at,
    updated_at
FROM tasks 
WHERE due_date IS NULL 
   OR created_at IS NULL 
   OR updated_at IS NULL;

-- 8. Verify date/time data types are correct
SELECT 
    column_name,
    data_type,
    datetime_precision
FROM information_schema.columns 
WHERE table_name = 'tasks' 
  AND column_name IN ('due_date', 'due_time', 'created_at', 'updated_at');

-- Expected Results:
-- - tasks table should exist with all required columns
-- - due_date should be 'date' type
-- - due_time should be 'time without time zone' type  
-- - created_at, updated_at should be 'timestamp without time zone' type
-- - RLS policies should be enabled
-- - Proper indexes should exist for performance