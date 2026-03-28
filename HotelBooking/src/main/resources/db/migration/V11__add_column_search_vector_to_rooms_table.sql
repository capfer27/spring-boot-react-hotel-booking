    ALTER TABLE rooms
    ADD COLUMN IF NOT EXISTS search_vector text
    GENERATED ALWAYS AS (
    room_number || ' ' ||
    room_type || ' ' ||
    CAST(price_per_night AS text) || ' ' ||
    CAST(capacity AS text) || ' ' ||
    COALESCE(description, '') -- Handle nulls safely
    ) STORED;