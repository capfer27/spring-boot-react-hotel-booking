export const Amenities = () => {
  const services = [
    { name: 'High-speed WiFi', icon: '/assets/wifi.png' },
    { name: 'Air Conditioning', icon: '/assets/ac.png' },
    { name: 'Parking Space', icon: '/assets/parking.png' },
    { name: 'Mini Bar', icon: '/assets/mini-bar.png' },
  ];

  return (
    <section className="max-w-7xl mx-auto px-4 py-16">
      <h2 className="text-3xl font-bold text-center text-gray-900 mb-12">
        Our <span className="text-blue-600">Services</span>
      </h2>

      <div className="grid grid-cols-2 md:grid-cols-4 gap-8">
        {services.map((service, index) => (
          <div
            key={index}
            className="flex flex-col items-center p-6 bg-white rounded-2xl shadow-sm hover:shadow-md transition-shadow border border-gray-100"
          >
            <img
              src={service.icon}
              alt={service.name}
              className="w-12 h-12 mb-4 object-contain"
            />
            <span className="text-gray-700 font-medium text-center">
              {service.name}
            </span>
          </div>
        ))}
      </div>
    </section>
  );
};
