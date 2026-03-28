package com.capfer.hotel.booking.controllers;

import com.capfer.hotel.booking.dtos.AddBookingRequest;
import com.capfer.hotel.booking.dtos.ResponseDTO;
import com.capfer.hotel.booking.dtos.RoomDTO;
import com.capfer.hotel.booking.dtos.UpdateBookingRequest;
import com.capfer.hotel.booking.enums.RoomType;
import com.capfer.hotel.booking.services.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "api/rooms", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping(path = "/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> addRoom(AddBookingRequest request) {

        RoomDTO roomDTO = RoomDTO.builder()
                .roomNumber(request.roomNumber())
                .roomType(request.roomType())
                .pricePerNight(request.pricePerNight())
                .capacity(request.capacity())
                .description(request.description())
                .build();

        ResponseDTO responseDTO = roomService.addRoom(roomDTO, request.imageFile());
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @PutMapping(path = "/update")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> updateRoom(UpdateBookingRequest request) {

        RoomDTO roomDTO = RoomDTO.builder()
                .id(request.roomId())
                .roomNumber(request.roomNumber())
                .roomType(request.roomType())
                .pricePerNight(request.pricePerNight())
                .capacity(request.capacity())
                .description(request.description())
                .build();

        ResponseDTO responseDTO = roomService.updateRoom(roomDTO, request.imageFile());
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<ResponseDTO> getAllRooms() {
        ResponseDTO allRooms = roomService.getAllRooms();
        return ResponseEntity.ok(allRooms);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ResponseDTO> getRoomById(@PathVariable(value = "id") Long roomId) {
        return ResponseEntity.ok(roomService.getRoomById(roomId));
    }

    @DeleteMapping(path = "/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> deleteRoom(@PathVariable(value = "id") Long roomId) {
        return ResponseEntity.ok(roomService.deleteRoom(roomId));
    }

    @GetMapping(path = "/available")
    public ResponseEntity<ResponseDTO> getAvailableRooms(
            @RequestParam(value = "checkInDate") LocalDate checkInDate,
            @RequestParam(value = "checkOutDate") LocalDate checkOutDate,
            @RequestParam(value = "roomType", required = false) RoomType roomType
    ) {
        ResponseDTO availableRooms = roomService.getAvailableRooms(checkInDate, checkOutDate, roomType);
        return ResponseEntity.ok(availableRooms);
    }

    @GetMapping(path = "/types")
    public ResponseEntity<List<RoomType>> getAllRoomTypes(){
        return ResponseEntity.ok(roomService.getAllRoomTypes());
    }

    @GetMapping(path = "/search")
    public ResponseEntity<ResponseDTO> searchRoom(@RequestParam(value = "term") String term) {
        return ResponseEntity.ok(roomService.searchRooms(term));
    }
}
