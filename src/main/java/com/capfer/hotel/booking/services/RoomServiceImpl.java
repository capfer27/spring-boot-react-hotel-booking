package com.capfer.hotel.booking.services;

import com.capfer.hotel.booking.dtos.ResponseDTO;
import com.capfer.hotel.booking.dtos.RoomDTO;
import com.capfer.hotel.booking.entities.Room;
import com.capfer.hotel.booking.enums.RoomType;
import com.capfer.hotel.booking.exceptions.InvalidBookingStateAndDateException;
import com.capfer.hotel.booking.exceptions.NotFoundException;
import com.capfer.hotel.booking.repositories.RoomRepository;
import com.capfer.hotel.booking.repositories.RoomSpecifications;
import com.capfer.hotel.booking.utils.RoomHelper;
import com.capfer.hotel.booking.utils.RoomValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private static final String IMAGE_UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/rooms/";

    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;

    public List<Room> searchAll(String searchTerm) {
        try (var scope = StructuredTaskScope.open(StructuredTaskScope.Joiner.allSuccessfulOrThrow())) {
            Subtask<List<Room>> hotelRoomTask = scope.fork(() -> {
                log.info("Executing task 1: findAll with search term '{}'", searchTerm);
                roomRepository.findAll(RoomSpecifications.globalSearchV3(searchTerm));
            });

            scope.join();
            return hotelRoomTask.get();

        } catch (InterruptedException | StructuredTaskScope.FailedException e) {
            log.error("Error occurred while fetching rooms: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch rooms", e);
        }
    }

    @Override
    public ResponseDTO addRoom(RoomDTO roomDTO, MultipartFile imageFile) {
        Room roomToSave = modelMapper.map(roomDTO, Room.class);

        try {
            Objects.requireNonNull(imageFile, "Image file must not be null");

            String imagePath = saveImage(imageFile);
            roomToSave.setImageUrl(imagePath);

            //Room savedRoom =
                    roomRepository.save(roomToSave);
            //RoomDTO savedRoomDTO = modelMapper.map(savedRoom, RoomDTO.class);

            return ResponseDTO.builder()
                    .message("Room added successfully")
                    .statusCode(HttpStatus.CREATED.value())
                    //.room(savedRoomDTO)
                    .build();
        } catch (IOException e) {
            log.error("Error occurred saving image file: {}", e.getMessage());
            return ResponseDTO.builder()
                    .message("Failed to save image file: " + e.getMessage())
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
        }
    }

    @Override
    public ResponseDTO updateRoom(RoomDTO roomDTO, MultipartFile imageFile) {
        try (var scope = StructuredTaskScope.open(StructuredTaskScope.Joiner.allSuccessfulOrThrow())) {
            Subtask<Room> updateRoomTask = scope.fork(() -> {
                log.info("Executing task: updateRoom for room ID '{}'", roomDTO.getId());
                return roomRepository.findById(roomDTO.getId()).get();
            });

            scope.join();

            if (updateRoomTask.get() == null) {
               log.info("Room with ID {} not found for update", roomDTO.getId());
                throw new NotFoundException("Room not found with ID: " + roomDTO.getId());
            }

            Room existingRoom = updateRoomTask.get();

            if (RoomValidator.isValidImageFile(imageFile)) {
                String imagePath = saveImage(imageFile);
                existingRoom.setImageUrl(imagePath);
            }

            if (RoomValidator.isValidRoomNumber(roomDTO.getRoomNumber())) {
                existingRoom.setRoomNumber(roomDTO.getRoomNumber());
            }

            if (RoomValidator.isValidPricePerNight(roomDTO.getPricePerNight())) {
                existingRoom.setPricePerNight(roomDTO.getPricePerNight());
            }

            if (RoomValidator.hasValidCapacity(roomDTO.getCapacity())) {
                existingRoom.setCapacity(roomDTO.getCapacity());
            }

            if (RoomValidator.isValidRoomType(roomDTO.getRoomType())) {
                existingRoom.setRoomType(roomDTO.getRoomType());
            }

            if (RoomValidator.hasValidDescription(roomDTO.getDescription())) {
                existingRoom.setDescription(roomDTO.getDescription());
            }

            //Room saved =
                    roomRepository.save(existingRoom);
            //var updatedRoom = modelMapper.map(saved, RoomDTO.class);

            log.info("Room {}, whose type {} was updated successfully", roomDTO.getRoomNumber(), roomDTO.getRoomType().name());

            return ResponseDTO.builder()
                    .message("Room updated successfully")
                    .statusCode(HttpStatus.OK.value())
                    //.room(updatedRoom)
                    .build();

        } catch (InterruptedException | StructuredTaskScope.FailedException e) {
            log.error("Error occurred while updating room: {}", e.getMessage());
            return ResponseDTO.builder()
                    .message("Failed to update room: " + e.getMessage())
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
        } catch (IOException e) {
            log.error("Error while saving image file: {}", e.getMessage());
            throw new RuntimeException("Failed to save image file", e);
        }
    }

    @Override
    public ResponseDTO getAllRooms() {
        try (var scope = StructuredTaskScope.open(StructuredTaskScope.Joiner.allSuccessfulOrThrow())) {
            Subtask<List<Room>> rooms = scope.fork(() -> {
                roomRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
            });

            scope.join();

            List<Room> foundRooms = rooms.get();
            List<RoomDTO> roomDTOS = RoomHelper.toRoomsDTOs(foundRooms, modelMapper);

            return ResponseDTO.builder()
                    .message("Rooms retrieved successfully")
                    .statusCode(HttpStatus.OK.value())
                    .rooms(roomDTOS)
                    .build();

        } catch (InterruptedException | StructuredTaskScope.FailedException e) {
            log.error("Error fetching all rooms: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch rooms", e);
        }
    }

    @Override
    public ResponseDTO getRoomById(Long roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(() ->
                new NotFoundException("Room not found for ID: " + roomId));

        RoomDTO roomDTO = modelMapper.map(room, RoomDTO.class);

        log.info("Room ID: {}, type: {} found successfully", room.getId(), room.getRoomType().name());

        return ResponseDTO.builder()
                .message("Room found successfully")
                .statusCode(HttpStatus.OK.value())
                .room(roomDTO)
                .build();
    }

    @Override
    public ResponseDTO deleteRoom(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            log.info("Room {} not found for deletion.", roomId);
            throw new NotFoundException("Room " + roomId + " does not exists");
        }

        roomRepository.deleteById(roomId);

        log.info("Room ID: {} deleted successfully", roomId);

        return ResponseDTO.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Room deleted successfully")
                .build();
    }

    @Override
    public ResponseDTO getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, RoomType roomType) {
        // Ensure check-in date is not before today
        if (checkInDate.isBefore(LocalDate.now())) {
            throw new InvalidBookingStateAndDateException("Check in date cannot be before today");
        }
        // Ensure check-out date is not before check-in date
        if (checkOutDate.isBefore(checkInDate)) {
            throw new InvalidBookingStateAndDateException("Check out date cannot be before check in date");
        }

        // Ensure check-in date is not same as check-in date
        if (checkInDate.isEqual(checkOutDate)) {
            throw new InvalidBookingStateAndDateException("Check in date cannot be same as check out date");
        }

        try (var source = StructuredTaskScope.open(StructuredTaskScope.Joiner.allSuccessfulOrThrow())) {

            Subtask<List<Room>> availableRooms = source.fork(() ->
                    roomRepository.findAvailableRoomsOptimized(checkInDate, checkOutDate, roomType)
            );
            
            source.join();

            List<RoomDTO> roomsDTOs = RoomHelper.toRoomsDTOs(availableRooms.get(), modelMapper);
            
            log.info("Found {} available rooms", roomsDTOs.size());
            
            return ResponseDTO.builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Available rooms found successfully")
                    .rooms(roomsDTOs)
                    .build();
        } catch (Exception e) {
            log.error("Failed to retrieve available rooms {}", e.getMessage());
            return ResponseDTO.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Error retrieving available rooms" + e.getMessage())
                    .build();
        }
    }

    @Override
    public List<RoomType> getAllRoomTypes() {
        return roomRepository.getAllRoomTypes();
    }

    @Override
    public ResponseDTO searchRooms(String searchTerm) {
        List<Room> rooms = searchAll(searchTerm);
        List<RoomDTO> roomDTOS = RoomHelper.toRoomsDTOs(rooms, modelMapper);

        log.info("Searching for: {}", searchTerm);
        log.info("Number of rooms found: {}", roomDTOS.size());

        return ResponseDTO.builder()
                .message("Rooms retrieved successfully")
                .statusCode(HttpStatus.OK.value())
                .rooms(roomDTOS)
                .build();
    }

    private String saveImage(MultipartFile imageFile) throws IOException {
        // Implement logic to save the image file to the server and return the file path
        // You can use a library like Apache Commons IO or Spring's FileCopyUtils to handle file saving
        if (imageFile.isEmpty()) {
            throw new IOException("No image file provided");
        }

        if (!Objects.requireNonNull(imageFile.getContentType()).startsWith("image/")) {
            throw new IOException("Invalid file type. Only image files are allowed.");
        }

        // Create the upload directory if it doesn't exist
        java.io.File uploadDir = new java.io.File(IMAGE_UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        String uniqueFileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();

        // Get absolute path to save the file
        String filePath = IMAGE_UPLOAD_DIR + uniqueFileName;

        try {

            // 1. Solution using Java NIO Files.copy
            // Files.copy(imageFile.getInputStream(), Path.of(filePath));

            File destinationFile = new File(filePath);
            imageFile.transferTo(destinationFile);

            // 2. Solution using Spring's FileCopyUtils
            // FileCopyUtils.copy(imageFile.getBytes(), new java.io.File(filePath));
            return filePath;
        } catch (Exception e) {
            log.error("Error saving image file: {}", e.getMessage());
            throw new IOException("Failed to save image file", e);
        }

    }
}
