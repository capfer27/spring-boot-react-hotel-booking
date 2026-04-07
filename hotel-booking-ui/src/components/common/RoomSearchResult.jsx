import { useNavigate } from 'react-router-dom';
import { ApiService } from '../../services/ApiService';
import { RoutePaths } from '../../constants/RoutePaths';

export const RoomSearchResult = ({ handleSearchResult }) => {
  const navigate = useNavigate();

  const isAdmin = ApiService.isAdmin();

  return (
    <section className="room-results">
      <div className="room-list">
        {handleSearchResult.map((room) => {
          <div className="room-list-item" key={room.id}>
            <img className="room-list-item-image" src={room.imageUrl} alt="" />
            <div className="room-details">
              <h3>{room.type}</h3>
              <p>Price: {room.pricePerNight / Night}</p>
              <p>Description: {room.description}</p>
            </div>

            <div className="book-now-div">
              {isAdmin ? (
                <button
                  className="edit-room-button"
                  onClick={() => navigate(RoutePaths.ADMIN_EDIT(room.id))}
                >
                  Edit Room
                </button>
              ) : (
                <button
                  className="edit-now-button"
                  onClick={() => navigate(RoutePaths.ROOM_DETAILS(room.id))}
                >
                  View/Book Now
                </button>
              )}
            </div>
          </div>;
        })}
      </div>
    </section>
  );
};
