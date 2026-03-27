-- Add the column as nullable first to avoid issues with existing rows
ALTER TABLE payments
ADD COLUMN stripe_payment_intent_id VARCHAR(255);

-- Ensure each Stripe ID is only used once in your system
ALTER TABLE payments
ADD CONSTRAINT uk_stripe_payment_intent_id UNIQUE (stripe_payment_intent_id);

-- Optional: Add a comment for clarity in the DB schema
COMMENT ON COLUMN payments.stripe_payment_intent_id IS 'Unique Stripe Payment Intent ID used for idempotency';