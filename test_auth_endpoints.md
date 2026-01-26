# Authentication Testing Guide

## Prerequisites
1. Run the database fix script first:
```sql
-- Execute complete_database_fix.sql in your database
```

2. Start your Spring Boot backend:
```bash
cd SelfGrowth_Backend
mvn spring-boot:run
```

## API Endpoints to Test

### Base URL: `http://localhost:8081/api/auth`

## 1. Regular Signup Test
**POST** `/signup`
```json
{
  "email": "test@example.com",
  "password": "TestPassword123",
  "confirmPassword": "TestPassword123",
  "fullName": "Test User",
  "age": 25,
  "profession": "Developer",
  "location": "Test City"
}
```

**Expected Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "username": "test",
    "email": "test@example.com",
    "fullName": "Test User",
    "token": "eyJ...",
    "message": "User registered successfully. Welcome to GrowPath!",
    "createdAt": "2024-..."
  }
}
```

## 2. Login Test
**POST** `/login`
```json
{
  "email": "test@example.com",
  "password": "TestPassword123"
}
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJ...",
    "expiresIn": 86400000,
    "userId": 1,
    "username": "test",
    "email": "test@example.com",
    "fullName": "Test User",
    "authId": "uuid-string",
    "message": "Login successful. Welcome back!"
  }
}
```

## 3. Google Sign-In Test
**POST** `/google-signin`
```json
{
  "idToken": "google-id-token-here",
  "email": "google@example.com"
}
```

**Expected Response (New User):**
```json
{
  "success": true,
  "message": "Google Sign-In processed",
  "data": {
    "token": null,
    "expiresIn": null,
    "userId": null,
    "username": null,
    "email": "google@example.com",
    "fullName": "Google User",
    "authId": null,
    "message": "Password setup required",
    "needsPasswordSetup": true
  }
}
```

## 4. Set Password Test (for Google users)
**POST** `/set-password`
```json
{
  "email": "google@example.com",
  "password": "GooglePassword123",
  "displayName": "Google User",
  "googleId": "google-user-id",
  "photoUrl": "https://photo-url.com"
}
```

## 5. Get Current User Test
**GET** `/me`
**Headers:** `Authorization: Bearer {token}`

## Testing Steps

### Step 1: Test Regular Signup
1. Use Postman/curl to test signup endpoint
2. Verify user is created in database
3. Verify JWT token is returned

### Step 2: Test Login
1. Use the same credentials from signup
2. Verify JWT token is returned
3. Verify user data is correct

### Step 3: Test Protected Endpoint
1. Use the JWT token from login
2. Call `/me` endpoint with Authorization header
3. Verify user data is returned

### Step 4: Test Google Sign-In Flow
1. Get a real Google ID token (from Android app)
2. Test google-signin endpoint
3. If needsPasswordSetup=true, test set-password endpoint

## Common Issues & Solutions

### Issue 1: Foreign Key Constraint Error
**Solution:** Run the database fix script first

### Issue 2: JWT Token Invalid
**Solution:** Check JWT secret in application.properties

### Issue 3: Google Token Verification Fails
**Solution:** Ensure Google Client ID is correct in application.properties

### Issue 4: CORS Issues
**Solution:** Check CrossOrigin annotation in controller

## Database Verification Queries

```sql
-- Check users table
SELECT id, email, username, auth_id, has_password, google_id FROM users;

-- Check for constraint issues
SELECT conname, contype FROM pg_constraint WHERE conrelid = 'users'::regclass;
```