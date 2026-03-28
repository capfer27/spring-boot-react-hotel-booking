CREATE EXTENSION IF NOT EXISTS btree_gist;

CREATE INDEX IF NOT EXISTS idx_room_booking_overlap
ON bookings USING gist (room_id, daterange(check_in_date, check_out_date))
WHERE booking_status IN ('BOOKED', 'CHECKED_IN');