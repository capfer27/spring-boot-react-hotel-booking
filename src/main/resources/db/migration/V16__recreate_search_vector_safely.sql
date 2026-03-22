-- 1. Ensure the extension exists for the index to work
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- 2. Drop existing index and column to allow recreation with COALESCE logic
DROP INDEX IF EXISTS idx_room_global_search;
ALTER TABLE rooms DROP COLUMN IF EXISTS search_vector;

-- 3. Add the column with full NULL safety (COALESCE)
-- We use ::text to ensure all types concatenate correctly
ALTER TABLE rooms
ADD COLUMN search_vector text
GENERATED ALWAYS AS (
    COALESCE(room_number::text, '') || ' ' ||
    COALESCE(room_type::text, '') || ' ' ||
    COALESCE(price_per_night::text, '') || ' ' ||
    COALESCE(capacity::text, '') || ' ' ||
    COALESCE(description, '')
) STORED;

-- 4. Re-create the GIN index for high-performance fuzzy search
CREATE INDEX idx_room_global_search
ON rooms
USING GIN (search_vector gin_trgm_ops);