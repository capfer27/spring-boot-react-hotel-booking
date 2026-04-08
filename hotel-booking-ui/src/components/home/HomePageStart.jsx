import { useState } from 'react';
import { RoomSearchResult } from '../common/original/RoomSearchResult';
import { RoomSearchV3 } from '../common/original/RoomSearchV3';
import { RoutePaths } from '../../constants/RoutePaths';
// import '../../index-backup.css';

export const HomePageStart = () => {
  const [roomSearchResult, setRoomSearchResult] = useState([]);

  // handle search result
  const handleSearchResult = (result) => {
    setRoomSearchResult(result);
  };

  return (
    <div className="home">
      <section>
        <header className="header-banner">
          <img
            src="./images/background.jpeg"
            alt="Hotel"
            className="header-image"
          />
          <div className="overlay"></div>
          <div className="animated-texts overlay-content">
            <h1>
              Welcome to <span className="phegon-color">Capfer Hotel</span>
            </h1>
            <h3>Step into a haven of confort and care</h3>
          </div>
        </header>
      </section>
      {/* Display the rooom search componet and room result */}
      <RoomSearchV3 handleSearchResult={handleSearchResult} />
      <RoomSearchResult roomSearchResult={roomSearchResult} />

      <h4>
        <a className="view-rooms-home" href={RoutePaths.ROOMS}>
          View All Rooms
        </a>
      </h4>
      <h2 className="home-services">
        Service at <span className="phegon-color">Carl Hotel</span>
      </h2>
      <section className="service-section">
        <div className="service-card">
          <img src="./assets/ac.png" alt="Air Conditioning" />
          <div className="service-details">
            <h3 className="services-title">Air Conditioning</h3>
            <p className="service-description">
              Stay cool and confortable throughout your stay with our
              individually controlled in room air conditioning
            </p>
          </div>
        </div>

        <div className="service-card">
          <img src="./assets/mini-bar.png" alt="Mini Bar" />
          <div className="service-details">
            <h3 className="services-title">Mini Bar</h3>
            <p className="service-description">
              Enjoy a convinient selection of beverages
            </p>
          </div>
        </div>

        <div className="service-card">
          <img src="./assets/parking.png" alt="Parking" />
          <div className="service-details">
            <h3 className="services-title">Parking</h3>
            <p className="service-description">
              We offer a cool parking space for your vehicle free of charge
            </p>
          </div>
        </div>

        <div className="service-card">
          <img src="./assets/wifi.png" alt="wifi" />
          <div className="service-details">
            <h3 className="services-title">WIFI</h3>
            <p className="service-description">
              Stay connected to our free wifi
            </p>
          </div>
        </div>
      </section>
    </div>
    // <Hero />
  );
};
