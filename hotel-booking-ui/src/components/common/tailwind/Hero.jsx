export const Hero1 = () => (
  <section className="relative h-[30vh] w-full bg-hero bg-cover bg-center flex items-center justify-center">
    {/* Dark Overlay */}
    <div className="absolute inset-0 bg-black/40" />

    <div className="relative z-10 text-center text-white space-y-4">
      <h1 className="text-5xl font-bold tracking-tight">
        Welcome to Carl Hotel
      </h1>
      <p className="text-lg font-light">
        Experience luxury in the heart of the city
      </p>
    </div>
  </section>
);

export const Hero = () => {
  return (
    /* The container must have a height (h-[60vh]) to show the background */
    <section
      className="relative h-[50vh] w-full flex items-center justify-center bg-gray-900 bg-cover bg-center bg-no-repeat"
      style={{
        /* Note the leading slash / - it is mandatory for public folder assets */
        backgroundImage: "url('/assets/background.jpeg')",
      }}
    >
      {/* Overlay for text readability */}
      <div className="absolute inset-0 bg-black/50 backdrop-blur-[2px]"></div>

      <div className="relative z-10 text-center px-4">
        <h1 className="text-4xl md:text-6xl font-bold text-white mb-4">
          Welcome to <span className="text-blue-500">Carl Hotel</span>
        </h1>
        <p className="text-lg text-gray-200">
          Luxury redefined in every detail.
        </p>
      </div>
    </section>
  );
};
