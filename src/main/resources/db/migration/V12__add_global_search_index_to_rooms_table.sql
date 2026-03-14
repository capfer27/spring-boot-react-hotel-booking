   CREATE INDEX IF NOT EXISTS idx_room_global_search
   ON rooms
   USING GIN (search_vector gin_trgm_ops);