package com.capfer.hotel.booking.services;

import com.capfer.hotel.booking.dtos.LoginRequest;
import com.capfer.hotel.booking.dtos.RegistrationRequest;
import com.capfer.hotel.booking.dtos.ResponseDTO;
import com.capfer.hotel.booking.dtos.UserDTO;
import com.capfer.hotel.booking.entities.User;

public interface UserService {

    ResponseDTO registerUser(RegistrationRequest registrationRequest);

    ResponseDTO loginUser(LoginRequest loginRequest);

    ResponseDTO getAllUsers();

    ResponseDTO getOwnAccountDetails();

    User getCurrentLoggedInUser();

    ResponseDTO updateOwnAccount(UserDTO userDTO);

    ResponseDTO deleteOnwAccount();

    ResponseDTO getMyBookingHistory();
}
