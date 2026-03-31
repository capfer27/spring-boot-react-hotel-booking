package com.capfer.hotel.booking.services;

import com.capfer.hotel.booking.dtos.*;
import com.capfer.hotel.booking.entities.Booking;
import com.capfer.hotel.booking.entities.User;
import com.capfer.hotel.booking.enums.UserRole;
import com.capfer.hotel.booking.exceptions.EmailAlreadyExistsException;
import com.capfer.hotel.booking.exceptions.InvalidCredentialException;
import com.capfer.hotel.booking.exceptions.NotFoundException;
import com.capfer.hotel.booking.repositories.BookingRepository;
import com.capfer.hotel.booking.repositories.UserRepository;
import com.capfer.hotel.booking.security.JwtUtilsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtilsService jwtUtilsService;

    private final ModelMapper modelMapper;

    @Override
    public ResponseDTO registerUser(RegistrationRequest registrationRequest) {

        if (userRepository.findByEmail(registrationRequest.getEmail()).isPresent()) {
            log.info("Email is already in use");
            throw new EmailAlreadyExistsException("Email is already in use");
        }

        try {
            UserRole userRole = UserRole.CUSTOMER;
            if (registrationRequest.getRole() != null) {
                userRole = registrationRequest.getRole();
            }

            User user = User.builder()
                    .firstName(registrationRequest.getFirstName())
                    .lastName(registrationRequest.getLastName())
                    .email(registrationRequest.getEmail())
                    .password(passwordEncoder.encode(registrationRequest.getPassword()))
                    .phoneNumber(registrationRequest.getPhoneNumber())
                    .role(userRole)
                    .isActive(true)
                    .build();

            userRepository.save(user);

            log.info("User name {}, whose email {} was registered successfully.", user.getFirstName(), user.getEmail());

            return ResponseDTO.builder()
                    .message("User registered successfully")
                    .statusCode(HttpStatus.CREATED.value())
                    .build();

        } catch (Exception e) {
            log.info("User registration failed: {}", e.getMessage());
            return ResponseDTO.builder()
                    .message("Registration failed")
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
        }
    }

    @Override
    public ResponseDTO loginUser(LoginRequest loginRequest) {
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new NotFoundException("User not found with email: " + loginRequest.getEmail()));

            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                throw new InvalidCredentialException("Password is not valid");
            }

            String token = jwtUtilsService.generateToken(user.getEmail());
            log.info("User with email {} logged in successfully.", user.getEmail());

        return ResponseDTO.builder()
                .statusCode(HttpStatus.OK.value())
                .message("User Logged in successful")
                .role(user.getRole())
                .token(token)
                .isActive(user.getIsActive())
                .expirationTime("6 months")
                .build();
    }

    @Override
    public ResponseDTO getAllUsers() {
        List<User> users = userRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        List<UserDTO> userDTOs = modelMapper.map(users, new TypeToken<List<UserDTO>>(){}.getType());

        log.info("Retrieved {} users from the database.", users.size());

        return ResponseDTO.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Users retrieved successfully")
                .users(userDTOs)
                .build();
    }

    /**
     * @return ResponseDTO containing the details of the currently authenticated user,
     * or an error message if the user is not authenticated.
     * This method is used to retrieve the user profile information.
     */
    @Override
    public ResponseDTO getOwnAccountDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
        String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

            UserDTO userDTO = modelMapper.map(user, UserDTO.class);

            log.info("Retrieved account details for user with email {}.", email);

            return ResponseDTO.builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Account details retrieved successfully")
                    .user(userDTO)
                    .build();
        }

        log.warn("Failed to retrieve account details: No authenticated user found.");

        return ResponseDTO.builder()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .message("User is not authenticated")
                .build();
    }

    /**
     * This method is used internally to retrieve the currently logged-in user from the security context.
     * @return User object representing the currently authenticated user.
     * @throws NotFoundException if no authenticated user is found in the security context.
     */
    @Override
    public User getCurrentLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String email = authentication.getName();
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
        }

        log.warn("Failed to retrieve current logged-in user: No authenticated user found.");

        throw new NotFoundException("No authenticated user found");
    }

    @Override
    public ResponseDTO updateOwnAccount(UserDTO userDTO) {
        User loggedInUser = getCurrentLoggedInUser();
        if (StringUtils.hasText(userDTO.getEmail())) {
            loggedInUser.setEmail(userDTO.getEmail());
        }

        if (StringUtils.hasText(userDTO.getFirstName())) {
            loggedInUser.setFirstName(userDTO.getFirstName());
        }

        if (StringUtils.hasText(userDTO.getLastName())) {
            loggedInUser.setLastName(userDTO.getLastName());
        }

        if (StringUtils.hasText(userDTO.getPhoneNumber())) {
            loggedInUser.setPhoneNumber(userDTO.getPhoneNumber());
        }

        if (StringUtils.hasText(userDTO.getPassword())) {
            loggedInUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        log.info("Updating account details for user with email {}.", loggedInUser.getEmail());

        userRepository.save(loggedInUser);

        return ResponseDTO.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Account updated successfully")
                .build();
    }

    @Override
    public ResponseDTO deleteOnwAccount() {
        User loggedInUser = getCurrentLoggedInUser();

        log.warn("Deleting account for user with email {}.", loggedInUser.getEmail());

        userRepository.delete(loggedInUser);

        return ResponseDTO.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Account deleted successfully")
                .build();
    }

    @Override
    public ResponseDTO getMyBookingHistory() {
        User loggedInUser = getCurrentLoggedInUser();

        List<Booking> bookings = bookingRepository.findByUserId(loggedInUser.getId());

        List<BookingDTO> bookingDTOS = modelMapper.map(bookings, new TypeToken<List<BookingDTO>>(){}.getType());

        log.info("Retrieved {} bookings for user with email {}.", bookings.size(), loggedInUser.getEmail());

        return ResponseDTO.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Booking history retrieved successfully")
                .bookings(bookingDTOS)
                .build();
    }
}
