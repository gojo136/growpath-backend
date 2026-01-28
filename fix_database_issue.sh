#!/bin/bash

echo "🔧 Fixing checkbox_data column type mismatch..."

# Step 1: Stop the Spring Boot application if running
echo "1. Stop your Spring Boot application first"

# Step 2: Connect to your PostgreSQL database and run this SQL
echo "2. Run this SQL in your PostgreSQL database:"
echo "   ALTER TABLE notes ALTER COLUMN checkbox_data TYPE TEXT;"
echo "   UPDATE notes SET checkbox_data = checkbox_data::text WHERE checkbox_data IS NOT NULL;"

# Step 3: Clean and rebuild the project
echo "3. Clean and rebuild the project:"
mvn clean compile

echo "4. Restart your Spring Boot application"

echo "✅ Fix completed! Your app should now work without the JSONB type mismatch error."