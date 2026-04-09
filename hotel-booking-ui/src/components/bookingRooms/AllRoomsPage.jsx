import { useEffect, useState } from 'react';
import { apiService } from '../../services/ApiService';
import SearchResult from '../common/tailwind/SearchResult';
import Pagination from '../common/tailwind/Pagination';
import RoomSearch from '../common/tailwind/RoomSearch';

const AllRoomsPage = () => {
  const [rooms, setRooms] = useState([]);
  const [roomTypes, setRoomTypes] = useState([]);
  const [roomType, setRoomType] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [roomsPerPage] = useState(20); // The limit per page
  const [totalPages, setTotalPages] = useState(0);

  // Fetch all available room types for the dropdown
  useEffect(() => {
    const fetchTypes = async () => {
      const types = await apiService.getRoomTypes();
      setRoomTypes(types);
    };

    fetchTypes();
  }, []);

  // Fetch rooms whenever filter or page changes

  useEffect(() => {
    fetchRooms();
  }, [roomType, currentPage]);

  const fetchRooms = async () => {
    // Make API call with pagination and types paramaters
    const response = await apiService.getAllRooms(
      currentPage - 1,
      roomsPerPage,
      roomType
    );
    console.log('All Rooms: ' + JSON.stringify(response));
    setRooms(response.rooms);
    console.log('totalPages: ', response.totalRoomPages);
    setTotalPages(response.totalRoomPages);
  };

  return (
    <div className="min-h-screen bg-gray-50 p-6 md:p-12">
      <div className="max-w-7xl mx-auto space-y-8">
        {/* Header Section */}
        <header className="text-center space-y-2">
          <h1 className="text-4xl font-extrabold text-gray-900 tracking-tight">
            Explore Our Rooms
          </h1>
          <p className="text-gray-500 text-lg">
            Find the perfect stay for your next adventure.
          </p>
        </header>

        {/* Search & Filter Bar */}
        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 space-y-6">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
            <div className="w-full md:w-1/3">
              <label className="block text-sm font-semibold text-gray-700 mb-2">
                Filter by Type
              </label>
              <select
                className="w-full p-3 rounded-lg border border-gray-200 bg-gray-50 focus:ring-2 focus:ring-blue-500 focus:outline-none transition-all"
                value={roomType}
                onChange={(e) => setRoomType(e.target.value)}
              >
                <option value="">All Room Types</option>
                {roomTypes.map((type) => (
                  <option key={type} value={type}>
                    {type}
                  </option>
                ))}
              </select>
            </div>

            <div className="w-full md:w-2/3">
              <RoomSearch handleSearch={fetchRooms} />
            </div>
          </div>
        </div>

        {/* Reuse Search Component */}
        {/* <div className="w-full md:w-2/3">
          <RoomSearch handleSearch={fetchRooms} />
        </div> */}

        {/* Dropdown filter */}
        {/* <div className="filter-section">
          <label> Filter by Room Type: </label>
          <select
            value={selectedType}
            onChange={(e) => setSelectedType(e.target.value)}
          >
            <option value="">All</option>
            {roomTypes.map((type) => (
              <option key={type} value={type}>
                {type}
              </option>
            ))}
          </select> */}

        {/* Card section with the results */}
        <main>
          <SearchResult rooms={rooms} />
        </main>

        {/* Only show pagination if there's more than one page */}
        {totalPages > 1 && (
          <Pagination
            currentPage={currentPage}
            totalPages={totalPages}
            onPageChange={setCurrentPage}
          />
        )}
      </div>
    </div>
    // </div>
  );
};

export default AllRoomsPage;
