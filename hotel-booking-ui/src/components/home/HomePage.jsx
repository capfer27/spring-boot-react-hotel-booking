import { useState } from 'react';
import { apiService } from '../../services/ApiService';
import RoomSearch from '../common/RoomSearch';
import SearchResult from '../common/SearchResult';
import { RoomSearchV3 } from '../common/original/RoomSearchV3';
import Pagination from '../common/Pagination';

const HomePage = () => {
  const [rooms, setRooms] = useState([]);
  const [searching, setSearching] = useState(false);
  const [searchQuery, setSearchQuery] = useState(null);

  // Required for pagination
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const limit = 20;

  // Triggered whenever the user clicks "Search" in the RoomSearch component
  const handleNewSearch = (query) => {
    setSearchQuery(query);
    setCurrentPage(1); // Reset to first page for a new search
    fetchData(query, 1);
  };

  // Triggered whenever a user clicks a page number in the Pagination component
  const handlePageChange = (page) => {
    fetchData(searchQuery, page);
  };

  const fetchData = async (query, page) => {
    if (!query) return;
    setLoading(true);
    try {
      // Spring Boot uses 0-based indexing: page - 1
      const { checkInDate, checkOutDate, roomType } = query;
      const response = await apiService.getAvailableRooms(
        checkInDate,
        checkOutDate,
        roomType,
        page - 1,
        limit // Number of rooms per page
      );

      // Spring Boot's Page object returns data in 'content'
      setRooms(response.rooms);
      setTotalPages(response.totalRoomPages);
      setCurrentPage(page);
    } catch (error) {
      console.error('Fetch failed', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearchPaging = async (query, page = 1) => {
    setSearching(true);
    // send pagination params to server
    try {
      const response = await apiService.getAvailableRooms({
        ...query,
        page: page - 1, // Spring Data JPA uses 0-based indexing
        size: limit, // Show 20 rooms per page
      });

      setRooms(response.rooms); // Spring Boot Page object has 'content'
      setTotalPages(response.totalRoomPages);
      setCurrentPage(page);
    } catch (error) {
      console.error('Paging Error Ocurred: ', error);
    } finally {
      setSearching(false);
    }
  };

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

  // return (
  //   <div className="bg-gray-50 min-h-screen">
  //     <RoomSearch onSearch={handleSearchPaging} />
  //     {/* <RoomSearchV3 onSearch={handleSearch} /> */}

  //     <div className="max-w-7xl mx-auto px-4 py-12">
  //       {searching ? (
  //         /* Global Search Loader */
  //         <div className="flex flex-col items-center justify-center py-20">
  //           <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-600 mb-4"></div>
  //           <p className="text-gray-500 font-medium">
  //             Searching for the best rooms...
  //           </p>
  //         </div>
  //       ) : (
  //         /* Search Results Grid */
  //         <SearchResult rooms={rooms} />
  //       )}
  //     </div>
  //   </div>
  // );
  return (
    <div className="bg-gray-50 min-h-screen pb-20">
      <RoomSearch onSearch={handleNewSearch} />

      <div className="max-w-7xl mx-auto px-4 mt-12">
        {loading ? (
          <div className="py-20 text-center">Loading rooms...</div>
        ) : (
          <>
            <SearchResult rooms={rooms} />

            {/* Only show pagination if there's more than one page */}
            {totalPages > 1 && (
              <Pagination
                currentPage={currentPage}
                totalPages={totalPages}
                onPageChange={handlePageChange}
              />
            )}
          </>
        )}
      </div>
    </div>
  );
};

export default HomePage;
