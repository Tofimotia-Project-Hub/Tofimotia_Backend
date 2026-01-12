-- Migration to add firstName, lastName, and phoneNumber columns to users table
-- Also adds profileData JSONB column for flexible role-specific data

-- Check if columns exist and add them if they don't
DO $$
BEGIN
    -- Add first_name if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name='users' AND column_name='first_name') THEN
        ALTER TABLE users ADD COLUMN first_name VARCHAR(100) NOT NULL DEFAULT '';
    END IF;

    -- Add last_name if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name='users' AND column_name='last_name') THEN
        ALTER TABLE users ADD COLUMN last_name VARCHAR(100) NOT NULL DEFAULT '';
    END IF;

    -- Add phone_number if it doesn't exist (note: different from 'phone')
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name='users' AND column_name='phone_number') THEN
        ALTER TABLE users ADD COLUMN phone_number VARCHAR(20) NOT NULL DEFAULT '';
    END IF;

    -- Add profile_data JSONB column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name='users' AND column_name='profile_data') THEN
        ALTER TABLE users ADD COLUMN profile_data JSONB;
    END IF;
END $$;

-- Remove default constraints after adding columns
ALTER TABLE users ALTER COLUMN first_name DROP DEFAULT;
ALTER TABLE users ALTER COLUMN last_name DROP DEFAULT;
ALTER TABLE users ALTER COLUMN phone_number DROP DEFAULT;

-- Create index on profile_data for better JSONB query performance
CREATE INDEX IF NOT EXISTS idx_users_profile_data ON users USING GIN (profile_data);

-- Add comment for documentation
COMMENT ON COLUMN users.profile_data IS 'Flexible JSONB field for role-specific profile data (e.g., specialization for doctors, license info for drivers)';
