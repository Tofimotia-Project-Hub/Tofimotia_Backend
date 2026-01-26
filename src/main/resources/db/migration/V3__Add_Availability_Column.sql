-- Migration to add availability column to venues table

-- Add availability column if it doesn't exist
ALTER TABLE venues ADD COLUMN IF NOT EXISTS availability JSONB;

-- Create index on availability for better JSONB query performance
CREATE INDEX IF NOT EXISTS idx_venues_availability ON venues USING GIN (availability);

-- Add comment for documentation
COMMENT ON COLUMN venues.availability IS 'JSONB field for venue availability schedules and time slots';