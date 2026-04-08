import RoomSearch from './RoomSearch';
import SearchResult from './SearchResult';
import Pagination from './Pagination';
import { Hero } from './Hero';

// TODO: This feature is not implemeted yet.
export const HomePageFeaturedRooms = ({
  currentPage,
  totalPages,
  handlePageChange,
  rooms,
  handleSearch,
}) => {
  return (
    <div className="bg-gray-50 min-h-screen">
      <Hero />
      <div className="-mt-12 md:-mt-16 px-4">
        <RoomSearch onSearch={handleSearch} />
      </div>

      {/* Featured Rooms Heading */}
      <div className="max-w-7xl mx-auto px-4 pt-20 pb-8 text-center">
        <h2 className="text-3xl md:text-5xl font-extrabold text-gray-900 tracking-tight mb-4">
          Our <span className="text-blue-600">Featured Rooms</span>
        </h2>
        <div className="w-24 h-1.5 bg-blue-600 mx-auto rounded-full mb-6"></div>
        <p className="text-gray-500 text-lg max-w-2xl mx-auto">
          Hand-picked luxury accommodations designed for ultimate comfort and
          relaxation.
        </p>
      </div>

      {/* Your Grid Component below */}
      <div className="max-w-7xl mx-auto px-4 pb-20">
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
    </div>
  );
};
