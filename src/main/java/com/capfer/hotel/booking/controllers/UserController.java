package com.capfer.hotel.booking.controllers;

import com.capfer.hotel.booking.dtos.ResponseDTO;
import com.capfer.hotel.booking.dtos.UserDTO;
import com.capfer.hotel.booking.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.awt.*;

@RestController
@RequestMapping(path = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(path = "/all")
    // Only users with the ADMIN role can access this endpoint
    @PreAuthorize(value = "hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> getAllUsers() {
        ResponseDTO responseDTO = userService.getAllUsers();
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping(path = "/account")
    public ResponseEntity<ResponseDTO> getAccountDetails() {
        ResponseDTO responseDTO = userService.getOwnAccountDetails();
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping(path = "/update")
    public ResponseEntity<ResponseDTO> updateOwnAccount(@RequestBody UserDTO userDTO) {
        ResponseDTO responseDTO = userService.updateOwnAccount(userDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping(value = "/delete")
    public ResponseEntity<ResponseDTO> deleteOwnAccount() {
        ResponseDTO responseDTO = userService.deleteOnwAccount();
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping(path = "/bookings")
    public ResponseEntity<ResponseDTO> getMyBookingHistory() {
        ResponseDTO responseDTO = userService.getMyBookingHistory();
        return ResponseEntity.ok(responseDTO);
    }
}
