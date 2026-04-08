import { useState } from 'react';
import { apiService } from '../../services/ApiService';
import RoomSearch from '../common/tailwind/RoomSearch';
import SearchResult from '../common/tailwind/SearchResult';
import Pagination from '../common/tailwind/Pagination';
import { Hero } from '../common/tailwind/Hero';
import { Amenities } from '../common/tailwind/Amenties';
import { HomePageFeaturedRooms } from '../common/tailwind/HomePageFeaturedRooms';
import { HomePageStart } from './HomePageStart';

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
  //   <div className="bg-gray-50 min-h-screen pb-20">
  //     <HomePageStart />
  //   </div>
  // );

  return (
    <div className="bg-gray-50 min-h-screen pb-20">
      <Hero />
      <RoomSearch onSearch={handleNewSearch} />
      <Amenities />

      <div className="max-w-7xl mx-auto px-4 mt-12">
        {loading ? (
          <div className="py-20 text-center">Loading rooms...</div>
        ) : (
          <div>
            <SearchResult rooms={rooms} />

            {/* Only show pagination if there's more than one page */}
            {totalPages > 1 && (
              <Pagination
                currentPage={currentPage}
                totalPages={totalPages}
                onPageChange={handlePageChange}
              />
            )}
          </div>
        )}
      </div>
      {/* <HomePageFeaturedRooms
        currentPage={currentPage}
        totalPages={totalPages}
        handlePageChange={handlePageChange}
        handleSearch={handleSearch}
      /> */}
    </div>
  );
};

export default HomePage;
