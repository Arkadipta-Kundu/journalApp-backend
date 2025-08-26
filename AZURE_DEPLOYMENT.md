# Azure Deployment Guide

## Required Azure App Service Environment Variables

When deploying to Azure App Service, configure these environment variables in the Azure Portal under **Configuration > Application Settings**:

### MongoDB Configuration

```
MONGODB_URI = mongodb+srv://votingsystem:zpC3Y8bB6ZOP9Z00@arkadev.prmhb.mongodb.net/?retryWrites=true&w=majority&appName=arkadev
MONGODB_DATABASE = journalAppDB
```

### Application Configuration

```
SESSION_TIMEOUT = 3600
SESSION_SECRET = your-super-secure-random-session-secret-key-here-min-32-chars
```

### Azure Specific Settings

```
WEBSITES_PORT = 8080
WEBSITES_CONTAINER_START_TIME_LIMIT = 600
SCM_DO_BUILD_DURING_DEPLOYMENT = true
```

## Deployment Steps

### 1. Prepare the Application

- ✅ Environment variables are configured in application.properties
- ✅ Sensitive data is excluded from Git via .gitignore
- ✅ MongoDB Atlas connection string will be set in Azure

### 2. Deploy to Azure App Service

#### Option A: Deploy from GitHub (Recommended)

1. Push your code to GitHub
2. In Azure Portal, create a new App Service
3. Choose **Java 21** as runtime stack
4. Choose **Maven** as build automation
5. Connect to your GitHub repository
6. Set the environment variables listed above
7. Deploy

#### Option B: Deploy via Azure CLI

```bash
# Login to Azure
az login

# Create resource group
az group create --name journalapp-rg --location "East US"

# Create App Service plan
az appservice plan create --name journalapp-plan --resource-group journalapp-rg --sku B1 --is-linux

# Create web app
az webapp create --resource-group journalapp-rg --plan journalapp-plan --name your-app-name --runtime "JAVA:21-java21"

# Configure environment variables
az webapp config appsettings set --resource-group journalapp-rg --name your-app-name --settings \
  MONGODB_URI="your-mongodb-connection-string" \
  MONGODB_DATABASE="journalAppDB" \
  SESSION_TIMEOUT="3600" \
  SESSION_SECRET="your-session-secret" \
  WEBSITES_PORT="8080"

# Deploy from GitHub
az webapp deployment source config --name your-app-name --resource-group journalapp-rg \
  --repo-url https://github.com/your-username/journalApp-backend --branch main --manual-integration
```

### 3. Configure MongoDB Atlas Network Access

1. In MongoDB Atlas Console, go to **Network Access**
2. Add IP Address: `0.0.0.0/0` (Allow access from anywhere)
   - Note: For production, restrict to Azure's IP ranges
3. Or add specific Azure datacenter IP ranges

### 4. Test the Deployment

```bash
# Test health endpoint
curl https://your-app-name.azurewebsites.net/public/api/health

# Test registration
curl -X POST https://your-app-name.azurewebsites.net/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"password123"}'
```

## Environment Variables Reference

| Variable           | Description                       | Example                                   | Required                      |
| ------------------ | --------------------------------- | ----------------------------------------- | ----------------------------- |
| `MONGODB_URI`      | MongoDB Atlas connection string   | `mongodb+srv://user:pass@cluster.net/...` | Yes                           |
| `MONGODB_DATABASE` | Database name                     | `journalAppDB`                            | No (default: journalAppDB)    |
| `SESSION_TIMEOUT`  | Session timeout in seconds        | `3600`                                    | No (default: 3600)            |
| `SESSION_SECRET`   | Secret key for session management | `super-secret-key`                        | Yes                           |
| `PORT`             | Server port                       | `8080`                                    | No (Azure sets automatically) |
| `WEBSITES_PORT`    | Azure specific port setting       | `8080`                                    | Recommended                   |

## Security Best Practices

### Session Secret Generation

Generate a secure session secret:

```bash
# Linux/Mac
openssl rand -base64 32

# Windows (PowerShell)
[System.Web.Security.Membership]::GeneratePassword(32, 0)
```

### MongoDB Security

1. Use strong database passwords
2. Enable MongoDB Atlas IP whitelist
3. Use dedicated database user for the application
4. Enable MongoDB Atlas audit logging

### Azure Security

1. Enable HTTPS only in Azure App Service
2. Use Azure Key Vault for sensitive secrets (advanced)
3. Enable Application Insights for monitoring
4. Configure custom domain with SSL certificate

## Monitoring and Troubleshooting

### Check Application Logs

```bash
# Stream logs
az webapp log tail --name your-app-name --resource-group journalapp-rg

# Download logs
az webapp log download --name your-app-name --resource-group journalapp-rg
```

### Common Issues

1. **Startup timeout**: Increase `WEBSITES_CONTAINER_START_TIME_LIMIT`
2. **Port issues**: Ensure `WEBSITES_PORT=8080` is set
3. **Database connection**: Verify MongoDB Atlas network access
4. **Environment variables**: Check they are set correctly in Azure Portal

## Cost Optimization

- Use **B1 Basic** plan for development/testing
- Use **S1 Standard** or higher for production
- Enable auto-scaling based on demand
- Monitor usage with Azure Cost Management

## Next Steps

After successful deployment:

1. Set up custom domain
2. Configure SSL certificate
3. Set up monitoring and alerts
4. Implement CI/CD pipeline
5. Add application insights
6. Configure backup strategies
