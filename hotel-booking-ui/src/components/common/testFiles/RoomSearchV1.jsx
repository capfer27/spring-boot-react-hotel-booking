import { useState, useEffect } from 'react';
import { HotelDatePickerV2 } from './HotelDatePickerV2';
import { apiService } from '../../../services/ApiService';

export const RoomSearchV1 = ({ onSerach }) => {
  const [selectedRange, setSelectedRange] = useState();

  const [roomTypes, setRoomTypes] = useState([]);

  // Load Dynamic DATA: Fetch room types from server on mount
  useEffect(() => {
    const fetchTypes = async () => {
      try {
        const types = await apiService.getRoomTypes();
        setRoomTypes(types);
      } catch (error) {
        console.error('Error fetching room types', error);
      } finally {
        // setIsTypesLoading(false);
      }
    };

    fetchTypes();
  }, []);

  const handleSearch = async (event) => {
    event.preventDefault();

    if (!selectedRange?.from || !selectedRange?.to) return;

    // Convert to ISO strings for your Spring Boot backend
    const searchData = {
      checkIn: selectedRange.from.toISOString(),
      checkOut: selectedRange.to.toISOString(),
      roomTypes: roomTypes,
    };

    try {
      const { checkInDate, checkOutDate, roomType } = searchData;
      const results = await apiService.getAvailableRooms(
        checkInDate,
        checkOutDate,
        roomType
      );
      //setRooms(results);
      onSerach(results);
    } catch (err) {
      console.error('Search failed', err);
    } finally {
      //setSearching(false);
    }
  };

  return (
    <div className="flex gap-4 min-h-screen items-center mx-auto mt-0 justify-center">
      <HotelDatePickerV2 onRangeSelect={setSelectedRange} />
      <button onClick={handleSearch}>Search</button>
    </div>
  );
};
