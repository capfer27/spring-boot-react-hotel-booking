export const Pagination = ({
  roomPerPage,
  totalRooms,
  currentPage,
  paginate,
}) => {
  const pageNumber = [];

  for (let i = 1; i < Math.ceil(totalRooms / roomPerPage); i++) {
    pageNumber.push(i);
  }

  return (
    <div className="pagination-nav">
      <ul className="pagination-ul">
        {pageNumber.map((itemNumber) => (
          <li key={itemNumber} className="pagination-li">
            <button
              onClick={() => paginate(itemNumber)}
              className={`pagination-button ${currentPage === itemNumber} ? 'current-page' : '' `}
            >
              {itemNumber}
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
};
