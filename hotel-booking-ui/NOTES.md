**_To implement these advanced features, 
we will integrate a dual-thumb price range slider and server-side pagination logic 
to ensure your app stays fast even with hundreds of rooms._**

1. Adding a Price Range Filter
   - Since standard HTML range inputs only support a single handle, 
   - a dual-thumb slider for "Min" and "Max" price is best 
   - handled with a small component logic.
   - RoomSearch.jsx

`const [priceRange, setPriceRange] = useState({ min: 0, max: 1000 })
const handlePriceChange = (e) => {
const { name, value } = e.target;
setPriceRange(prev => ({ ...prev, [name]: parseInt(value) }));
};

// In your JSX within the form grid:
<div className="space-y-1 col-span-full md:col-span-1">
    <label className="text-xs font-semibold text-gray-500 uppercase">
        Price: ${priceRange.min} - ${priceRange.max}
    </label>
    <div className="flex gap-2 items-center">
        <input 
            type="range" name="min" min="0" max="1000" step="50"
            value={priceRange.min} onChange={handlePriceChange}
            className="w-full h-1.5 bg-gray-200 rounded-lg appearance-none cursor-pointer accent-blue-600"
        />
        <input 
            type="range" name="max" min="0" max="1000" step="50"
            value={priceRange.max} onChange={handlePriceChange}
            className="w-full h-1.5 bg-gray-200 rounded-lg appearance-none cursor-pointer accent-blue-600"
        />
    </div>
</div>`

2. Professional Pagination Component
   - For the pagination UI, use a separate component that receives the current page and total pages from your Spring Boot Page object.
   - src/components/Pagination.jsx

`const Pagination = ({ currentPage, totalPages, onPageChange }) => {
 const pages = Array.from({ length: totalPages }, (_, i) => i + 1);

    return (
        <nav className="flex justify-center items-center space-x-2 py-8">
            <button 
                disabled={currentPage === 1}
                onClick={() => onPageChange(currentPage - 1)}
                className="p-2 rounded-lg border border-gray-300 disabled:opacity-30 hover:bg-gray-100 transition-colors"
            >
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 19l-7-7 7-7" /></svg>
            </button>

            {pages.map(page => (
                <button
                    key={page}
                    onClick={() => onPageChange(page)}
                    className={`w-10 h-10 rounded-lg font-semibold transition-all ${
                        currentPage === page 
                            ? "bg-blue-600 text-white shadow-lg shadow-blue-200" 
                            : "border border-gray-300 text-gray-600 hover:bg-gray-100"
                    }`}
                >
                    {page}
                </button>
            ))}

            <button 
                disabled={currentPage === totalPages}
                onClick={() => onPageChange(currentPage + 1)}
                className="p-2 rounded-lg border border-gray-300 disabled:opacity-30 hover:bg-gray-100 transition-colors"
            >
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 5l7 7-7 7" /></svg>
            </button>
        </nav>
    );
};
`
3. Integrated Page Logic
   In your HomePage.jsx, pass the page and size as parameters to your Axios request.
`const [currentPage, setCurrentPage] = useState(1);
   const [totalPages, setTotalPages] = useState(0);

const handleSearch = async (query, page = 1) ⇒ {
setSearching(true);
try {
// Send pagination params to Spring Boot
const response = await apiService.searchAvailableRooms({
    ...query,
    page: page - 1, // Spring Data JPA uses 0-based indexing
    size: 6         // Show 6 rooms per page
});

        setRooms(response.content); // Spring Boot Page object has 'content'
        setTotalPages(response.totalPages);
        setCurrentPage(page);
    } catch (err) {
        console.error(err);
    } finally {
        setSearching(false);
    }
};
`
    