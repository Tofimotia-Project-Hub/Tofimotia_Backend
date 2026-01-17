# Fix Database URL for Railway Deployment

## Issue
The application is failing to start because it cannot resolve the database hostname `postgres.railway.internal`.

## Solution
Update the DATABASE_URL environment variable in Railway to use the external hostname.

### Current DATABASE_URL (incorrect):
```
jdbc:postgresql://postgres.railway.internal:5432/railway?sslmode=disable
```

### Correct DATABASE_URL (should be):
```
jdbc:postgresql://trolley.proxy.rlwy.net:30232/railway?sslmode=require
```

## How to Fix

### Option 1: Using Railway CLI
```bash
railway variables set DATABASE_URL="jdbc:postgresql://trolley.proxy.rlwy.net:30232/railway?sslmode=require"
```

### Option 2: Using Railway Dashboard
1. Go to your Railway project dashboard
2. Navigate to the tofimotia-backend service
3. Go to Variables tab
4. Update the DATABASE_URL variable to: `jdbc:postgresql://trolley.proxy.rlwy.net:30232/railway?sslmode=require`
5. Save the changes

### Option 3: Alternative - Use Railway's Database Service URL
If you have a Railway PostgreSQL service, you can also use:
```bash
railway variables set DATABASE_URL=${{Postgres.DATABASE_URL}}
```

## After Fixing
Once the DATABASE_URL is updated, Railway will automatically redeploy the application and it should start successfully.

## Verification
After the fix, you can verify the deployment by checking:
- Health endpoint: https://tofimotia-backend-production.up.railway.app/actuator/health
- Swagger UI: https://tofimotia-backend-production.up.railway.app/swagger-ui.html