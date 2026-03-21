package com.capfer.hotel.booking.utils;

import com.capfer.hotel.booking.dtos.RoomDTO;
import com.capfer.hotel.booking.entities.Room;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.util.List;

public final class RoomHelper {

    private RoomHelper() {}

    public static List<RoomDTO> toRoomsDTOs(List<Room> rooms, ModelMapper mapper) {
        return mapper.map(rooms, new TypeToken<List<RoomDTO>>(){}.getType());
    }

    public static RoomDTO toRoomDTO(Room room, ModelMapper mapper) {
        return mapper.map(room, RoomDTO.class);
    }
}
