# Testing Environment Variables in Azure

## 🧪 How to Test Environment Variables in Azure

Once your application is deployed to Azure, you can test if it's properly reading environment variables using these diagnostic endpoints:

### **1. Basic Health Check**

```bash
curl https://your-app-name.azurewebsites.net/public/api/health
```

**Expected Response:**

```json
{
  "status": "healthy",
  "message": "Journal App Backend is running",
  "timestamp": "2025-08-26T12:15:30",
  "version": "1.0.0-PHASE1"
}
```

### **2. Application Info with Environment Detection**

```bash
curl https://your-app-name.azurewebsites.net/public/api/info
```

**Expected Response (in Azure):**

```json
{
  "name": "Journal App Backend",
  "description": "A secure journaling backend with social features",
  "phase": "Phase 1 - Core Features",
  "features": [
    "User Registration & Login",
    "Journal Entry CRUD",
    "Basic Authentication",
    "Password Hashing"
  ],
  "environment": "Azure App Service",
  "env_vars_status": {
    "mongodb_uri_set": true,
    "session_secret_set": true,
    "websites_port_set": true
  }
}
```

### **3. Detailed Configuration Info**

```bash
curl https://your-app-name.azurewebsites.net/public/api/config-info
```

**Expected Response (in Azure):**

```json
{
  "database_name": "journalAppDB",
  "session_timeout": "3600",
  "server_port": "8080",
  "mongodb_uri_configured": true,
  "mongodb_uri_length": 150,
  "session_secret_configured": true,
  "session_secret_length": 32,
  "environment": "Azure App Service",
  "environment_variables": {
    "MONGODB_URI": "[SET - Length: 150]",
    "MONGODB_DATABASE": "[SET - Length: 11]",
    "SESSION_SECRET": "[SET - Length: 32]",
    "SESSION_TIMEOUT": "[SET - Length: 4]",
    "WEBSITES_PORT": "[SET - Length: 4]"
  }
}
```

### **4. Environment Variables Test**

```bash
curl https://your-app-name.azurewebsites.net/public/api/env-test
```

**Expected Response (in Azure):**

```json
{
  "MONGODB_URI": "✅ SET",
  "MONGODB_DATABASE": "✅ SET",
  "SESSION_SECRET": "✅ SET",
  "SESSION_TIMEOUT": "✅ SET",
  "PORT": "✅ SET",
  "WEBSITES_PORT": "✅ SET",
  "WEBSITE_HOSTNAME": "✅ SET"
}
```

## 🔍 **What to Look For**

### **If Environment Variables are Working:**

- ✅ `environment` should show "Azure App Service"
- ✅ All `env_vars_status` should be `true`
- ✅ `mongodb_uri_configured` should be `true`
- ✅ `session_secret_configured` should be `true`
- ✅ All items in `/env-test` should show "✅ SET"

### **If Environment Variables are NOT Working:**

- ❌ `environment` might show "Unknown"
- ❌ `env_vars_status` will have `false` values
- ❌ `/env-test` will show "❌ NOT SET" for missing variables
- ❌ Application logs will show "NO" for configured variables

## 📋 **Startup Logs to Check**

In Azure Portal → Your App Service → Log Stream, look for:

```
=== Journal App Backend Startup Info ===
Detected environment: Azure App Service
MongoDB Database: journalAppDB
Server Port: 8080
MONGODB_URI configured: YES
SESSION_SECRET configured: YES
Azure-specific variables:
  WEBSITES_PORT: 8080
  WEBSITE_HOSTNAME: your-app-name.azurewebsites.net
=== Startup Info Complete ===
```

## 🛠️ **Troubleshooting**

### **If Variables are NOT SET:**

1. Check Azure Portal → Configuration → Application Settings
2. Verify the environment variable names match exactly:

   - `MONGODB_URI`
   - `MONGODB_DATABASE`
   - `SESSION_SECRET`
   - `SESSION_TIMEOUT`
   - `WEBSITES_PORT`

3. Restart the App Service after adding variables

### **If Variables are SET but Application Fails:**

1. Check the Log Stream for specific error messages
2. Verify MongoDB connection string format
3. Ensure MongoDB Atlas network access allows Azure IPs

## 🎯 **Test Sequence**

1. **Deploy to Azure**
2. **Set Environment Variables in Azure Portal**
3. **Restart App Service**
4. **Test `/public/api/health`** - Should work immediately
5. **Test `/public/api/info`** - Check environment detection
6. **Test `/public/api/env-test`** - Verify all variables are set
7. **Test `/public/api/config-info`** - Get detailed configuration
8. **Check Log Stream** - Look for startup messages
9. **Test actual functionality** - Try registering a user

This comprehensive testing approach will definitively show you whether Azure is properly providing the environment variables to your application! 🎉
