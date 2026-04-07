import { useState } from 'react';
import { apiService } from '../../services/ApiService';
import RoomSearch from '../common/RoomSearch';
import { RoomSearchV1 } from '../common/RoomSearchV1';

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
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8">
            {rooms.length > 0 ? (
              rooms.map((room) => (
                <div
                  key={room.id}
                  className="bg-white rounded-2xl overflow-hidden shadow-sm hover:shadow-xl transition-shadow duration-300 border border-gray-100 flex flex-col"
                >
                  <img
                    src={room.imageUrl}
                    alt={room.roomType}
                    className="h-48 w-full object-cover"
                  />
                  <div className="p-6 flex flex-col grow">
                    <div className="flex justify-between items-start mb-2">
                      <h3 className="text-xl font-bold text-gray-900">
                        {room.roomType} Room
                      </h3>
                      <span className="bg-blue-50 text-blue-700 text-xs font-bold px-2 py-1 rounded uppercase tracking-wider">
                        {room.roomType}
                      </span>
                    </div>
                    <p className="text-gray-500 text-sm mb-6 grow">
                      {room.description}
                    </p>
                    <div className="flex justify-between items-center mt-auto pt-4 border-t border-gray-50">
                      <span className="text-2xl font-black text-blue-600">
                        ${room.pricePerNight}
                        <span className="text-sm font-normal text-gray-400">
                          /night
                        </span>
                      </span>
                      <button className="bg-gray-900 text-white px-5 py-2 rounded-lg font-semibold hover:bg-blue-600 transition-colors">
                        Book Now
                      </button>
                    </div>
                  </div>
                </div>
              ))
            ) : (
              <p className="col-span-full text-center text-gray-400 py-20 italic">
                No rooms found for your criteria. Try different dates!
              </p>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default HomePage;
