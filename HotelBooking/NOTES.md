```shell
docker compose --profile fully-dockerized up --build
```

How to optimize global search using Postgres GIN Index instead of default B-Tree Index. 
This can significantly improve search performance for large datasets.
Steps:
1. Enable the Extension
   ```postgresql
     CREATE EXTENSION IF NOT EXISTS pg_trgm;
   ```  
   You must run this once in your database to enable efficient fuzzy text search.
2. The "Silver Bullet" Strategy: Generated Column
      Searching 5 different columns with OR is slow because the database often has to check multiple indexes or scan the heap.
      The fix is to create a single column that "contains" all the data and index that.
      SQL Migration:
      2.1. Add a generated column that auto-updates when data changes
    ```postgresql
    ALTER TABLE room
    ADD COLUMN search_vector text
    GENERATED ALWAYS AS (
    room_number || ' ' ||
    room_type || ' ' ||
    CAST(price_per_night AS text) || ' ' ||
    CAST(capacity AS text) || ' ' ||
    COALESCE(description, '') -- Handle nulls safely
    ) STORED;
    

   ```
   -- 2. Create a single GIN index on this column
   ```postgresql
   CREATE INDEX idx_room_global_search
   ON rooms
   USING GIN (search_vector gin_trgm_ops); 
   ```
3. Index for the Booking Table
   The subquery is the most expensive part. 
   A multicolum index (composite index) on the Booking table 
   will allow Postgres to quickly find overlapping dates for specific statuses.
```postgres-sql
    CREATE INDEX idx_booking_dates_status_room 
    ON bookings (checkInDate, checkOutDate, bookingStatus, room_id);
```
 Why? 
 This covers the WHERE clause and includes room_id so Postgres can perform 
 an Index Only Scan, avoiding the need to read the actual table rows 
 to get the IDs for the NOT IN filter. 

Index for the Room Table
For the outer query, a partial index is highly effective if you often filter by roomType.
```postgresql
CREATE INDEX idx_room_type
    ON rooms (roomType)
    WHERE roomType IS NOT NULL;
```
4. Query Optimization
   Pro-Tip: Optimize the SQL Logic
   PostgreSQL often handles NOT EXISTS better than NOT IN when dealing with nulls or large datasets. 
   If performance is still slow, consider refactoring the JPQL/SQL:
```postgresql
-- Logic shift: "Find rooms where NO booking overlaps"
SELECT r FROM Room r
WHERE (r.roomType IS NULL OR r.roomType = :roomType)
AND NOT EXISTS (
    SELECT 1 FROM Booking b 
    WHERE b.room = r 
    AND b.checkInDate <= :checkOutDate 
    AND b.checkOutDate >= :checkInDate
    AND b.bookingStatus IN ('BOOKED', 'CHECKED_IN')
)
```    

5. Recommended Postgres Index:
To make this query instant even with millions of rows, 
add a GiST index using the btree_gist extension to handle the "overlap" efficiently:
```postgresql
CREATE EXTENSION IF NOT EXISTS btree_gist;

CREATE INDEX idx_room_booking_overlap 
ON bookings USING gist (room_id, daterange(check_in_date, check_out_date))
WHERE booking_status IN ('BOOKED', 'CHECKED_IN');

```

```postgresql
SELECT NOT EXISTS (
    SELECT 1 
    FROM bookings
    WHERE room_id = :roomId
      AND booking_status IN ('BOOKED', 'CHECKED_IN')
      -- Overlap logic: Start < End AND End > Start
      AND check_in_date < :checkOutDate 
      AND check_out_date > :checkInDate
);

```

