#!/bin/bash

echo "🚀 Tofimotia Production Deployment Script"
echo "=========================================="

# Check if environment variables are set
check_env_vars() {
    echo "📋 Checking environment variables..."
    
    if [ -z "$DATABASE_URL" ]; then
        echo "❌ DATABASE_URL not set"
        return 1
    fi
    
    if [ -z "$JWT_SECRET" ]; then
        echo "❌ JWT_SECRET not set"
        return 1
    fi
    
    echo "✅ Environment variables are set"
    return 0
}

# Build the application
build_app() {
    echo "🔨 Building application..."
    ./mvnw clean package -DskipTests
    
    if [ $? -eq 0 ]; then
        echo "✅ Build successful"
        return 0
    else
        echo "❌ Build failed"
        return 1
    fi
}

# Run database migrations
run_migrations() {
    echo "🗄️ Running database migrations..."
    ./mvnw flyway:migrate -Dspring.profiles.active=prod
    
    if [ $? -eq 0 ]; then
        echo "✅ Database migrations completed"
        return 0
    else
        echo "❌ Database migrations failed"
        return 1
    fi
}

# Deploy to Railway
deploy_railway() {
    echo "🚂 Deploying to Railway..."
    
    if ! command -v railway &> /dev/null; then
        echo "Installing Railway CLI..."
        npm install -g @railway/cli
    fi
    
    railway login
    railway up
}

# Deploy to Render
deploy_render() {
    echo "🎨 Deploying to Render..."
    echo "Push your code to GitHub and connect it to Render using render.yaml"
    echo "Render will automatically deploy when you push to main branch"
}

# Main deployment function
main() {
    echo "Select deployment platform:"
    echo "1) Railway"
    echo "2) Render"
    echo "3) Manual (just build and migrate)"
    
    read -p "Enter your choice (1-3): " choice
    
    case $choice in
        1)
            if check_env_vars && build_app && run_migrations; then
                deploy_railway
            fi
            ;;
        2)
            if build_app; then
                deploy_render
            fi
            ;;
        3)
            if check_env_vars && build_app && run_migrations; then
                echo "✅ Application built and database migrated successfully"
                echo "🚀 You can now deploy the JAR file: target/Tofimotia-0.0.1-SNAPSHOT.jar"
            fi
            ;;
        *)
            echo "❌ Invalid choice"
            exit 1
            ;;
    esac
}

# Run main function
main