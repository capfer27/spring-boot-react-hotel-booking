import React from 'react';
import { useNavigate } from 'react-router-dom';
import { RoutePaths } from '../../../constants/RoutePaths';
import { isEmptyResponse } from '../../../utils/apiUtils';

const SearchResult = ({ rooms }) => {
  const navigate = useNavigate();
  console.log('ROOMS: ', rooms);

  // If no rooms are passed or the array is empty
  if (isEmptyResponse(rooms)) {
    return (
      <div className="text-center py-20 bg-white rounded-2xl border border-dashed border-gray-300">
        <p className="text-gray-400 italic">
          No rooms found for your criteria. Try different dates!
        </p>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8">
      {rooms.map((room) => (
        <div
          key={room.id}
          className="bg-white rounded-2xl overflow-hidden shadow-sm hover:shadow-xl transition-all duration-300 border border-gray-100 flex flex-col group"
        >
          {/* Room Image */}
          <div className="relative overflow-hidden h-48">
            <img
              src={'/assets/' + room.imageUrl}
              alt={room.roomType}
              className="h-full w-full object-cover group-hover:scale-105 transition-transform duration-500"
            />
            <div className="absolute top-4 right-4">
              <span className="bg-white/90 backdrop-blur-sm text-blue-700 text-xs font-bold px-2 py-1 rounded uppercase tracking-wider shadow-sm">
                {room.roomType}
              </span>
            </div>
          </div>

          {/* Room Info */}
          <div className="p-6 flex flex-col grow">
            <h3 className="text-xl font-bold text-gray-900 mb-2">
              {room.roomType} Room
            </h3>
            <p className="text-gray-500 text-sm line-clamp-2 mb-6 grow">
              {room.description}
            </p>

            <div className="flex justify-between items-center mt-auto pt-4 border-t border-gray-50">
              <div>
                <span className="text-2xl font-black text-blue-600">
                  ${room.pricePerNight}
                </span>
                <span className="text-sm font-normal text-gray-400">
                  /night
                </span>
              </div>

              <button
                onClick={() => navigate(RoutePaths.ROOM_DETAILS(room.id))}
                className="bg-gray-900 text-white px-5 py-2 rounded-lg font-semibold hover:bg-blue-600 transition-colors transform active:scale-95"
              >
                View/Book Now
              </button>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
};

export default SearchResult;
