-- Supabase Storage Setup for Notes App
-- Run this in Supabase SQL Editor

-- 1. Create storage buckets
INSERT INTO storage.buckets (id, name, public) 
VALUES 
  ('note-images', 'note-images', true),
  ('note-audio', 'note-audio', true);

-- 2. Set up RLS policies for note-images bucket
CREATE POLICY "Users can upload images" ON storage.objects
  FOR INSERT WITH CHECK (
    bucket_id = 'note-images' AND 
    auth.uid()::text = (storage.foldername(name))[1]
  );

CREATE POLICY "Users can view own images" ON storage.objects
  FOR SELECT USING (
    bucket_id = 'note-images' AND 
    auth.uid()::text = (storage.foldername(name))[1]
  );

CREATE POLICY "Users can delete own images" ON storage.objects
  FOR DELETE USING (
    bucket_id = 'note-images' AND 
    auth.uid()::text = (storage.foldername(name))[1]
  );

-- 3. Set up RLS policies for note-audio bucket
CREATE POLICY "Users can upload audio" ON storage.objects
  FOR INSERT WITH CHECK (
    bucket_id = 'note-audio' AND 
    auth.uid()::text = (storage.foldername(name))[1]
  );

CREATE POLICY "Users can view own audio" ON storage.objects
  FOR SELECT USING (
    bucket_id = 'note-audio' AND 
    auth.uid()::text = (storage.foldername(name))[1]
  );

CREATE POLICY "Users can delete own audio" ON storage.objects
  FOR DELETE USING (
    bucket_id = 'note-audio' AND 
    auth.uid()::text = (storage.foldername(name))[1]
  );

-- 4. Enable RLS on storage.objects
ALTER TABLE storage.objects ENABLE ROW LEVEL SECURITY;