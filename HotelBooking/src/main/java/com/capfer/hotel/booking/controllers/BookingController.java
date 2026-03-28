package com.capfer.hotel.booking.controllers;

import com.capfer.hotel.booking.dtos.BookingDTO;
import com.capfer.hotel.booking.dtos.ResponseDTO;
import com.capfer.hotel.booking.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/bookigns", produces = MediaType.APPLICATION_JSON_VALUE)
public class BookingController {

    private final BookingService bookingService;

    @GetMapping(path = "/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @PostMapping(path = "/create")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('CUSTOMER')")
    public ResponseEntity<ResponseDTO> createBooking(@RequestBody BookingDTO bookingDTO) {
        return new ResponseEntity<>(bookingService.createBooking(bookingDTO), HttpStatus.CREATED);
    }

    @GetMapping(path = "/{reference}")
    public ResponseEntity<ResponseDTO> getByReference(@PathVariable String reference) {
        return ResponseEntity.ok(bookingService.findBookingByReferenceNumber(reference));
    }

    @PutMapping(path = "/update")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> updateBooking(@RequestBody BookingDTO bookingDTO) {
        return new ResponseEntity<>(bookingService.updateBooking(bookingDTO), HttpStatus.OK);
    }
}
