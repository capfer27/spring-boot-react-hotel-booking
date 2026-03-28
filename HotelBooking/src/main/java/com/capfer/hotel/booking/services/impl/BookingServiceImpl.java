package com.capfer.hotel.booking.services.impl;

import com.capfer.hotel.booking.dtos.BookingDTO;
import com.capfer.hotel.booking.dtos.NotificationDTO;
import com.capfer.hotel.booking.dtos.ResponseDTO;
import com.capfer.hotel.booking.entities.Booking;
import com.capfer.hotel.booking.entities.Room;
import com.capfer.hotel.booking.entities.User;
import com.capfer.hotel.booking.enums.BookingStatus;
import com.capfer.hotel.booking.enums.PaymentStatus;
import com.capfer.hotel.booking.exceptions.InvalidBookingStateAndDateException;
import com.capfer.hotel.booking.exceptions.NotFoundException;
import com.capfer.hotel.booking.repositories.BookingRepository;
import com.capfer.hotel.booking.repositories.RoomRepository;
import com.capfer.hotel.booking.services.BookingReferenceService;
import com.capfer.hotel.booking.services.BookingService;
import com.capfer.hotel.booking.services.NotificationService;
import com.capfer.hotel.booking.services.UserService;
import com.capfer.hotel.booking.utils.BookingHelper;
import com.capfer.hotel.booking.utils.RoomHelper;
import com.capfer.hotel.booking.utils.RoomValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.StructuredTaskScope;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private static final String BASE_URL = "http://localhost:3000/payment/";

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final NotificationService notificationService;
    private final ModelMapper mapper;
    private final UserService userService;
    private final BookingReferenceService bookingReferenceService;

    @Override
    @Transactional
    public ResponseDTO createBooking(BookingDTO bookingDTO) {
        User currentLoggedInUser = userService.getCurrentLoggedInUser();
        Room room = roomRepository.findById(bookingDTO.getRoomId())
                .orElseThrow(() ->
                        new NotFoundException("Can't create booking for non-existing room ID: "
                                + bookingDTO.getRoomId()));

        RoomValidator.validateBooking(bookingDTO.getCheckInDate(), bookingDTO.getCheckOutDate());

        boolean roomAvailable =
                bookingRepository.isRoomAvailable(room.getId(), bookingDTO.getCheckInDate(), bookingDTO.getCheckOutDate());

        if (!roomAvailable) {
            log.info("Available rooms not found for the selected dates ranges: {} and {}", bookingDTO.getCheckInDate(), bookingDTO.getCheckOutDate());
            throw new InvalidBookingStateAndDateException("Available rooms not found for those dates");
        }


        // Generate and save the booking reference first
        final String bookingReference = bookingReferenceService.generateBookingReference();
        bookingReferenceService.saveBookingReferenceToDB(bookingReference);

        // Calculate the total price needed to pay for the stay.
        BigDecimal totaPriceToPay = RoomHelper.calculateTotaPrice(room, bookingDTO);

        // Create and save booking
        Booking newBooking = getNewBooking(bookingDTO, currentLoggedInUser, room, totaPriceToPay, bookingReference);

        log.info("NEW BOOKING: {}", newBooking);

        Booking savedBooking = bookingRepository.save(newBooking);

        // Generate the payment url which will be sent via email
        final String paymentLink = BASE_URL + bookingReference + "/" + totaPriceToPay;

        log.info("GENERATED PAYMENT LINK: {}", paymentLink);

        // Send email notification
        NotificationDTO notificationDTO = createBookingConfirmation(currentLoggedInUser, paymentLink, bookingReference);

        log.info("Sending email notification to customer {} after successful booking.", currentLoggedInUser.getEmail());

        notificationService.sendEmail(notificationDTO);

        BookingDTO bookingDTOResult = mapper.map(savedBooking, BookingDTO.class);

        return ResponseDTO.builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Booking created successfully.")
                .booking(bookingDTOResult)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseDTO findBookingByReferenceNumber(String bookingReference) {
        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new NotFoundException("Booking with reference Number " + bookingReference + " was not found"));

        BookingDTO bookingDTO = mapper.map(booking, BookingDTO.class);

        return ResponseDTO.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Booking found successfully")
                .booking(bookingDTO)
                .build();
    }

    @Override
    @Transactional
    public ResponseDTO updateBooking(BookingDTO bookingDTO) {

        if (Objects.isNull(bookingDTO.getId())) {
            throw new NotFoundException("Booking is ID required");
        }

        Booking booking = bookingRepository.findById(bookingDTO.getId())
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (Objects.nonNull(bookingDTO.getBookingStatus())) {
            booking.setBookingStatus(bookingDTO.getBookingStatus());
        }

        if (Objects.nonNull(bookingDTO.getPaymentStatus())) {
            booking.setPaymentStatus(bookingDTO.getPaymentStatus());
        }

        log.info("Updating booking {}", booking);

        bookingRepository.save(booking);

        return ResponseDTO.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Booking updated successfully")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseDTO getAllBookings() {
        try(var scope = StructuredTaskScope.open(StructuredTaskScope.Joiner.awaitAllSuccessfulOrThrow())) {

            //bookingRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))
            StructuredTaskScope.Subtask<List<Booking>> bookings = scope.fork(bookingRepository::findAllWithDetails);

            scope.join();

            List<Booking> bookingList = bookings.get();
            List<BookingDTO> bookingDTOS = BookingHelper.toDTOs(bookingList, mapper, true);

            return ResponseDTO.builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Booking fetched successfully")
                    .bookings(bookingDTOS)
                    .build();

        } catch (Exception e) {
            log.error("Failed to retrieve all bookings {}", e.getMessage());
            return ResponseDTO.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Failed to fetch all bookings")
                    .build();
        }
    }

    private static NotificationDTO createBookingConfirmation(User currentLoggedInUser, String paymentLink, String bookingReference) {
        return NotificationDTO.builder()
                .recipient(currentLoggedInUser.getEmail())
                .subject("Booking Confirmation")
                .body(String.format("Your booking has been created successfully. " +
                        "Please, proceed with your payment using the payment link below \n\n%s", paymentLink
                ))
                .bookingReference(bookingReference)
                .build();
    }

    private static Booking getNewBooking(
            BookingDTO bookingDTO,
            User currentLoggedInUser,
            Room room,
            BigDecimal totaPriceToPay,
            String bookingReference
    ) {
        return Booking.builder()
                .user(currentLoggedInUser)
                .room(room)
                .checkInDate(bookingDTO.getCheckInDate())
                .checkOutDate(bookingDTO.getCheckOutDate())
                .totalPrice(totaPriceToPay)
                .bookingReference(bookingReference)
                .bookingStatus(BookingStatus.BOOKED)
                .paymentStatus(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

}
