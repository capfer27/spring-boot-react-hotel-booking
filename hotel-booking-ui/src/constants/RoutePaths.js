export const RoutePaths = {
  HOME: '/home',
  ROOT: '/',
  LOGIN: '/login',
  REGISTER: '/register',
  ROOMS: '/rooms',
  PROFILE: '/profile',
  MY_BOOKINGS: '/my-bookings',
  FIND_BOOKING: '/find-bookings',
  ADMIN: '/admin',
  ADMIN_EDIT: (id) => `/ADMIN/edit/${id}`,
  // Dynamic routes
  // ROOM_DETAILS: (id) => `/rooms/${id}`,
  // BOOKING_DETAILS: (ref) => `/bookings/${ref}`,
  ROOM_DETAILS: (id) => `/room/${id}`,
  SETTINGS: '/settings',
};
