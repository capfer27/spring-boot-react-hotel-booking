import { useEffect, useState } from 'react';
import { apiService } from '../../../services/ApiService';

const RoomSearch = ({ onSearch }) => {
  const [searchQuery, setSearchQuery] = useState({
    checkInDate: '',
    checkOutDate: '',
    roomType: '',
  });

  const [roomTypes, setRoomTypes] = useState([]);
  const [error, setError] = useState('');
  const [isTypesLoading, setIsTypesLoading] = useState(true);

  // Load Dynamic DATA: Fetch room types from server on mount
  useEffect(() => {
    const fetchTypes = async () => {
      try {
        const types = await apiService.getRoomTypes();
        setRoomTypes(types);
      } catch (error) {
        console.error('Error fetching room types', error);
      } finally {
        setIsTypesLoading(false);
      }
    };

    fetchTypes();
  }, []);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setSearchQuery({ ...searchQuery, [name]: value });
    // console.log(name, value, searchQuery);
    if (error) {
      setError(''); // Clear error when user types.
    }
  };

  const handleSearch = (event) => {
    event.preventDefault();
    const { checkInDate, checkOutDate, roomType } = searchQuery;

    // Dynamic validation: ensure dates are logical
    if (!checkInDate || !checkOutDate || !roomType) {
      setError('Please fill in all fields');
      return;
    }

    if (new Date(checkInDate) >= new Date(checkOutDate)) {
      setError('Check-out date must be after check-in date.');
      return;
    }

    // Pass clean data back to parent component to trigger API call
    onSearch(searchQuery);
  };

  return (
    <section className="bg-white p-6 rounded-xl shadow-md border border-gray-100 mt-12 max-w-4xl mx-auto relative z-10">
      {isTypesLoading ? (
        <div className="flex justify-center items-center py-4 text-blue-600">
          <svg className="animate-spin h-6 w-6" viewBox="0 0 24 24">
            <circle
              className="opacity-25"
              cx="12"
              cy="12"
              r="10"
              stroke="currentColor"
              strokeWidth="4"
              fill="none"
            />
            <path
              className="opacity-75"
              fill="currentColor"
              d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
            />
          </svg>
          <span className="ml-2 font-medium">Loading room inventory...</span>
        </div>
      ) : (
        <form
          onSubmit={handleSearch}
          className="grid grid-cols-1 md:grid-cols-4 gap-4 items-end"
        >
          {/* Check-In */}
          <div className="space-y-1">
            <label className="text-xs font-semibold text-gray-500 uppercase">
              Check-In
            </label>
            <input
              type="date"
              name="checkInDate"
              min={new Date().toISOString().split('T')[0]} // Prevents past dates
              className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
              onChange={handleChange}
            />
          </div>

          {/* Check-Out */}
          <div className="space-y-1">
            <label className="text-xs font-semibold text-gray-500 uppercase">
              Check-Out
            </label>
            <input
              type="date"
              name="checkOutDate"
              min={
                searchQuery.checkInDate ||
                new Date().toISOString().split('T')[0]
              }
              className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
              onChange={handleChange}
            />
          </div>

          {/* Dynamic Room Types */}
          <div className="space-y-1">
            <label className="text-xs font-semibold text-gray-500 uppercase">
              Room Type
            </label>
            <select
              name="roomType"
              className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none bg-white"
              onChange={handleChange}
            >
              <option value="">Select Type</option>
              {roomTypes.map((type, idx) => (
                <option key={idx} value={type}>
                  {type}
                </option>
              ))}
            </select>
          </div>

          {/* Search Button */}
          <button className="bg-blue-600 text-white font-bold py-2 px-6 rounded-lg hover:bg-blue-700 transition-all shadow-lg shadow-blue-200 h-10.5">
            Search Rooms
          </button>
        </form>
      )}

      {error && (
        <p className="text-red-500 text-xs mt-3 text-center font-medium animate-pulse">
          {error}
        </p>
      )}
    </section>
  );
};

export default RoomSearch;
