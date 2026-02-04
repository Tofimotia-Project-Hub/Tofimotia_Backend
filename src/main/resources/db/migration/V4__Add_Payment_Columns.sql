-- Add missing columns to payments table for enhanced payment tracking
ALTER TABLE payments 
ADD COLUMN IF NOT EXISTS reference VARCHAR(255),
ADD COLUMN IF NOT EXISTS gateway_response TEXT,
ADD COLUMN IF NOT EXISTS failure_reason TEXT;

-- Update existing payment records to have default values
UPDATE payments 
SET reference = CONCAT('REF_', id, '_', EXTRACT(EPOCH FROM created_at)::bigint)
WHERE reference IS NULL;

-- Ensure payment_method column can handle new enum values (expand if needed)
ALTER TABLE payments ALTER COLUMN payment_method TYPE VARCHAR(50);

-- Ensure status column can handle new enum values (expand if needed)  
ALTER TABLE payments ALTER COLUMN status TYPE VARCHAR(50);

-- Add indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_payments_reference ON payments(reference);
CREATE INDEX IF NOT EXISTS idx_payments_status ON payments(status);