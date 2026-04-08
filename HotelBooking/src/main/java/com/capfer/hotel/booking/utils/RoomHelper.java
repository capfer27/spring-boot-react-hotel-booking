package com.capfer.hotel.booking.utils;

import com.capfer.hotel.booking.dtos.BookingDTO;
import com.capfer.hotel.booking.dtos.RoomDTO;
import com.capfer.hotel.booking.entities.Room;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

public final class RoomHelper {

    private RoomHelper() {}

    public static List<RoomDTO> toRoomsDTOs(List<Room> rooms, ModelMapper mapper) {
        return mapper.map(rooms, new TypeToken<List<RoomDTO>>(){}.getType());
    }

    public static RoomDTO toRoomDTO(Room room, ModelMapper mapper) {
        return mapper.map(room, RoomDTO.class);
    }

    public static BigDecimal calculateTotaPrice(Room room, BookingDTO bookingDTO) {
        BigDecimal pricePerNight = room.getPricePerNight();
        long days = ChronoUnit.DAYS.between(bookingDTO.getCheckInDate(), bookingDTO.getCheckOutDate());
        return pricePerNight.multiply(BigDecimal.valueOf(days));
    }

    public static List<RoomDTO> toRoomsDTOs(Page<Room> rooms, ModelMapper mapper) {
        return mapper.map(rooms.getContent(), new TypeToken<List<RoomDTO>>(){}.getType());
    }
}
