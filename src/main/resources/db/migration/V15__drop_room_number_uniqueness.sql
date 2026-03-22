-- Flyway migration to drop unique constraint on room_number
ALTER TABLE rooms DROP CONSTRAINT rooms_room_number_key;