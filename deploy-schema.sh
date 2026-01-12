#!/bin/bash

echo "Deploying database schema to Railway PostgreSQL..."

# Get the database URL from Railway
DB_URL=$(railway variables --json | jq -r '.DATABASE_PUBLIC_URL')

if [ "$DB_URL" = "null" ] || [ -z "$DB_URL" ]; then
    echo "Error: Could not get database URL from Railway"
    exit 1
fi

echo "Database URL obtained successfully"

# Run the schema migration
echo "Running schema migration..."
psql "$DB_URL" -f src/main/resources/db/migration/V1__Initial_Schema.sql

if [ $? -eq 0 ]; then
    echo "✅ Database schema deployed successfully!"
    echo "🚀 Your production database is ready!"
else
    echo "❌ Schema deployment failed"
    exit 1
fi