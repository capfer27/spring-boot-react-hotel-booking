import { useEffect, useRef, useState } from 'react';
import { apiService } from '../../services/ApiService';
import { HttpStatusCode } from 'axios';
import { isEmptyResponse } from '../../utils/apiUtils';
import { DayPicker } from 'react-day-picker';
// import '../../index-backup.css';

export const RoomSearchV3 = ({ handleSearchResult }) => {
  const [startDate, setStartDate] = useState(null);
  const [endDate, setEnddate] = useState(null);
  const [roomType, setRoomType] = useState('');

  const [roomTypes, setRoomTypes] = useState([]);
  const [error, setError] = useState('');

  // state for controlling calendar visibility
  const [isStartDatePickerVisible, setStartDatePickerVisible] = useState(false);
  const [isEndDatePickerVisible, setEndDatePickerVisible] = useState(false);

  const startDateRef = useRef(null);
  const endDateRef = useRef(null);

  useEffect(() => {
    const fetchTypes = async () => {
      try {
        const types = await apiService.getRoomTypes();
        setRoomTypes(types);
      } catch (error) {
        console.error('Failed to fetch room types', error);
      }
    };

    fetchTypes();
  }, []);

  const handleClickOutside = (e) => {
    if (startDateRef.current && !startDateRef.current.contains(e.target)) {
      setStartDatePickerVisible(false);
    }

    if (endDateRef.current && !endDateRef.current.contains(e.target)) {
      setEndDatePickerVisible(false);
    }
  };

  useEffect(() => {
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const showError = (message, timeout = 5000) => {
    setError(message);
    setTimeout(() => {
      setError('');
    }, timeout);
  };

  const handleRoomSearch = async () => {
    if (!startDate || !endDate || !roomType) {
      showError('Please fill in all fields');
      return false;
    }

    try {
      const formattedStartDate = startDate
        ? startDate.toLocaleDateString('en-US')
        : null;
      const formattedEndDate = endDate
        ? endDate.toLocaleDateString('en-US')
        : null;

      const result = await apiService.getAvailableRooms({
        formattedStartDate,
        formattedEndDate,
        roomType,
      });

      if (result.statusCode === HttpStatusCode.Ok) {
        if (isEmptyResponse(result.rooms)) {
          showError('Room type not currently available for the selected date.');
          return;
        }
        handleSearchResult(response.rooms);
        showError('');
      }
    } catch (error) {
      showError(error?.response?.data?.message || error.message);
    }
  };

  return (
    <section>
      <div className="search-container">
        {/* For check-in date and calendar field */}
        <div className="search-field" style={{ position: 'relative' }}>
          <label htmlFor="checkInDate">Check-In Date</label>
          <input
            type="text"
            value={startDate ? startDate.toLocaleDateString() : ''}
            placeholder="Select Check-In Date"
            onFocus={() => setStartDatePickerVisible(true)}
            readOnly
          />
          {isStartDatePickerVisible && (
            <div className="datepicker-container" ref={startDateRef}>
              <DayPicker
                selected={startDate}
                onDayClick={(date) => {
                  setStartDate(date);
                  setStartDatePickerVisible(false);
                }}
                month={startDate}
              />
            </div>
          )}
        </div>

        {/* For check-out date and calendar field */}
        <div className="search-field" style={{ position: 'relative' }}>
          <label htmlFor="checkInDate">Check-Out Date</label>
          <input
            type="text"
            value={endDate ? endDate.toLocaleDateString() : ''}
            placeholder="Select Check-Out Date"
            onFocus={() => setEndDatePickerVisible(true)}
            readOnly
          />
          {isEndDatePickerVisible && (
            <div className="datepicker-container" ref={endDateRef}>
              <DayPicker
                selected={endDate}
                onDayClick={(date) => {
                  setEnddate(date);
                  setEndDatePickerVisible(false);
                }}
                month={endDate}
              />
            </div>
          )}
        </div>

        {/* For Room Types  */}
        <div className="search-field">
          <label htmlFor="roomtype">Room Type</label>
          <select
            name="roomtype"
            id="roomtype"
            value={roomType}
            onChange={(e) => {
              setRoomType(e.target.value);
            }}
          >
            <option value="">Select Room Type</option>
            {roomTypes.map((type, idIndex) => (
              <option key={idIndex} value={type}>
                {type}
              </option>
            ))}
          </select>
        </div>

        <button className="home-search-button" onClick={handleRoomSearch}>
          Search Rooms
        </button>

        {error && <p className="error-message">{error}</p>}
      </div>
    </section>
  );
};
