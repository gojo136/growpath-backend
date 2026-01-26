package org.example;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnectionTest {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://aws-1-ap-south-1.pooler.supabase.com:5432/postgres?sslmode=require";
        String username = "postgres.lgnmjminzdlkorgfbtyo";
        String password = "@ay#Ashutosh0589";
        
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("✅ SUCCESS: Connected to Supabase database!");
            System.out.println("Database: " + conn.getCatalog());
            System.out.println("SSL Enabled: Yes");
            conn.close();
        } catch (Exception e) {
            System.out.println("❌ FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
