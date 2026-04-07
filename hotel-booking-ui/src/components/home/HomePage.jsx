import { useState } from 'react';
import { apiService } from '../../services/ApiService';
import RoomSearch from '../common/RoomSearch';
import SearchResult from '../common/SearchResult';
import { RoomSearchV3 } from '../common/RoomSearchV3';

const HomePage = () => {
  const [rooms, setRooms] = useState([]);
  const [searching, setSearching] = useState(false);

  const handleSearch = async (query) => {
    setSearching(true);
    try {
      const { checkInDate, checkOutDate, roomType } = query;
      const results = await apiService.getAvailableRooms(
        checkInDate,
        checkOutDate,
        roomType
      );
      setRooms(results);
    } catch (err) {
      console.error('Search failed', err);
    } finally {
      setSearching(false);
    }
  };

  return (
    <div className="bg-gray-50 min-h-screen">
      <RoomSearch onSearch={handleSearch} />
      {/* <RoomSearchV3 onSearch={handleSearch} /> */}

      <div className="max-w-7xl mx-auto px-4 py-12">
        {searching ? (
          /* Global Search Loader */
          <div className="flex flex-col items-center justify-center py-20">
            <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-600 mb-4"></div>
            <p className="text-gray-500 font-medium">
              Searching for the best rooms...
            </p>
          </div>
        ) : (
          /* Search Results Grid */
          <SearchResult rooms={rooms} />
        )}
      </div>
    </div>
  );
};

export default HomePage;
