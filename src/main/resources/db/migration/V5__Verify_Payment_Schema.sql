-- Verify and ensure all payment-related columns exist with correct types
-- This migration ensures compatibility with the Payment entity

-- Ensure all required columns exist with proper constraints
DO $$
BEGIN
    -- Check if reference column exists, if not add it
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'payments' AND column_name = 'reference') THEN
        ALTER TABLE payments ADD COLUMN reference VARCHAR(255);
    END IF;
    
    -- Check if gateway_response column exists, if not add it
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'payments' AND column_name = 'gateway_response') THEN
        ALTER TABLE payments ADD COLUMN gateway_response TEXT;
    END IF;
    
    -- Check if failure_reason column exists, if not add it
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'payments' AND column_name = 'failure_reason') THEN
        ALTER TABLE payments ADD COLUMN failure_reason TEXT;
    END IF;
END $$;

-- Ensure payment_method and status columns can handle enum values
ALTER TABLE payments 
ALTER COLUMN payment_method TYPE VARCHAR(50),
ALTER COLUMN status TYPE VARCHAR(50);

-- Add constraints to ensure data integrity
ALTER TABLE payments 
ALTER COLUMN amount SET NOT NULL,
ALTER COLUMN status SET NOT NULL,
ALTER COLUMN payment_method SET NOT NULL;

-- Create indexes if they don't exist
CREATE INDEX IF NOT EXISTS idx_payments_reference ON payments(reference);
CREATE INDEX IF NOT EXISTS idx_payments_status ON payments(status);
CREATE INDEX IF NOT EXISTS idx_payments_method ON payments(payment_method);