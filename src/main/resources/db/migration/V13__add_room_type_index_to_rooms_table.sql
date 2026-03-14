CREATE INDEX IF NOT EXISTS idx_room_type
ON rooms (room_type)
WHERE room_type IS NOT NULL;