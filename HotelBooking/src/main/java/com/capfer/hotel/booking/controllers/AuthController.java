package com.capfer.hotel.booking.controllers;

import com.capfer.hotel.booking.dtos.LoginRequest;
import com.capfer.hotel.booking.dtos.RegistrationRequest;
import com.capfer.hotel.booking.dtos.ResponseDTO;
import com.capfer.hotel.booking.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping(path = "/register")
    public ResponseEntity<ResponseDTO> register(@RequestBody @Valid RegistrationRequest request) {

        ResponseDTO registeredUser = userService.registerUser(request);

        return ResponseEntity.status(HttpStatus.CREATED.value())
                .body(registeredUser);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody @Valid LoginRequest request) {
        ResponseDTO loggedInUser = userService.loginUser(request);
        return ResponseEntity.ok(loggedInUser);
    }
}
