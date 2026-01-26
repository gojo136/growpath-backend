package org.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SupabaseStorageConfig {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.anon.key}")
    private String supabaseAnonKey;

    @Value("${supabase.service.key}")
    private String supabaseServiceKey;

    @Value("${supabase.storage.bucket.images:note-images}")
    private String imagesBucket;

    @Value("${supabase.storage.bucket.audio:note-audio}")
    private String audioBucket;

    public String getSupabaseUrl() { return supabaseUrl; }
    public String getSupabaseAnonKey() { return supabaseAnonKey; }
    public String getSupabaseServiceKey() { return supabaseServiceKey; }
    public String getImagesBucket() { return imagesBucket; }
    public String getAudioBucket() { return audioBucket; }

    public String getStorageUrl() {
        return supabaseUrl + "/storage/v1";
    }

    public String getPublicUrl(String bucket, String fileName) {
        return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + fileName;
    }
}