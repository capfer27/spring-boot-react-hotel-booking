package com.capfer.hotel.booking.services;

import com.capfer.hotel.booking.entities.Room;
import com.capfer.hotel.booking.repositories.RoomRepository;
import com.capfer.hotel.booking.repositories.RoomSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public List<Room> findAll(String searchTerm) {
        return roomRepository.findAll(RoomSpecifications.globalSearchV3(searchTerm));
    }
}
