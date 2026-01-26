# Final Testing Checklist

## Before Testing - Database Setup

1. **Run Database Fix Script:**
```sql
-- Execute this in your PostgreSQL/Supabase database
-- Step 1: Drop problematic constraints
ALTER TABLE public.users DROP CONSTRAINT IF EXISTS users_auth_id_fkey;
ALTER TABLE public.users DROP CONSTRAINT IF EXISTS fk_users_auth_id;

-- Step 2: Ensure auth_id column exists
ALTER TABLE public.users ADD COLUMN IF NOT EXISTS auth_id UUID;

-- Step 3: Update existing users
UPDATE public.users 
SET auth_id = gen_random_uuid() 
WHERE auth_id IS NULL;

-- Step 4: Disable RLS
ALTER TABLE public.users DISABLE ROW LEVEL SECURITY;
ALTER TABLE public.notes DISABLE ROW LEVEL SECURITY;

-- Step 5: Verify fix
SELECT id, email, auth_id, has_password FROM public.users LIMIT 5;
```

## Testing Steps

### 1. Start Backend
```bash
cd SelfGrowth_Backend
mvn spring-boot:run
```
**Expected:** Server starts on port 8081

### 2. Test Backend Endpoints (Optional - using Postman/curl)
```bash
# Test signup
curl -X POST http://localhost:8081/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"TestPassword123","confirmPassword":"TestPassword123","fullName":"Test User","age":25}'

# Test login  
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"TestPassword123"}'
```

### 3. Test Android App

#### A. Regular Signup Flow
1. Open Android app
2. Go to Signup screen
3. Fill form:
   - Email: `test2@example.com`
   - Password: `TestPassword123`
   - Confirm Password: `TestPassword123`
   - Full Name: `Test User 2`
   - Age: `25`
4. Tap "Sign Up"
5. **Expected:** Success → Navigate to Dashboard

#### B. Login Flow
1. Go to Login screen
2. Enter credentials from signup
3. Tap "Login"
4. **Expected:** Success → Navigate to Dashboard

#### C. Google Sign-In Flow
1. Go to Login screen
2. Tap "Sign in with Google"
3. Complete Google authentication
4. **Expected:** 
   - If new user: Navigate to password setup
   - If existing user: Navigate to Dashboard

## What Information I Need From You

### If Testing Fails:

1. **Error Logs:** Share the exact error message from:
   - Android Studio Logcat
   - Spring Boot console
   - Database error (if any)

2. **Which Step Failed:**
   - Database setup
   - Backend startup
   - Specific authentication flow (signup/login/google)

3. **Network Issues:** 
   - Can you access `http://10.0.2.2:8081` from Android emulator?
   - Are you using emulator or physical device?

### For Google Sign-In:
- Is your Google Client ID configured correctly?
- Are you testing on emulator or physical device?
- Do you have the correct SHA-1 fingerprint registered?

## Quick Fixes for Common Issues

### Issue 1: "Connection refused"
**Solution:** Ensure backend is running and use correct IP:
- Emulator: `10.0.2.2:8081`
- Physical device: Your computer's IP address

### Issue 2: "Foreign key constraint violation"
**Solution:** Run the database fix script above

### Issue 3: "JWT token invalid"
**Solution:** Check if JWT secret matches in application.properties

### Issue 4: Google Sign-In fails
**Solution:** Verify Google Client ID and SHA-1 fingerprint

## Ready to Test?

1. ✅ Run database fix script
2. ✅ Start Spring Boot backend
3. ✅ Updated Android ApiClient (no RLS)
4. ✅ Test authentication flows

**Let me know which step fails and I'll help fix it immediately!**