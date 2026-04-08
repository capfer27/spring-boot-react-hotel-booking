package com.capfer.hotel.booking.services;

import com.capfer.hotel.booking.dtos.ResponseDTO;
import com.capfer.hotel.booking.dtos.RoomDTO;
import com.capfer.hotel.booking.enums.RoomType;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface RoomService {

    ResponseDTO addRoom(RoomDTO roomDTO, MultipartFile imageFile);

    ResponseDTO updateRoom(RoomDTO roomDTO, MultipartFile imageFile);

    ResponseDTO getAllRooms();

    ResponseDTO getRoomById(Long roomId);

    ResponseDTO deleteRoom(Long roomId);

    ResponseDTO getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, RoomType roomType, Integer page, Integer size);

    List<RoomType> getAllRoomTypes();

    ResponseDTO searchRooms(String searchTerm);

}
