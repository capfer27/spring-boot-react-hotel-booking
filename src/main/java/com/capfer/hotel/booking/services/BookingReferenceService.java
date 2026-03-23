package com.capfer.hotel.booking.services;

import com.capfer.hotel.booking.entities.BookingReference;
import com.capfer.hotel.booking.repositories.BookingReferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

/**
 * TODO: Get rid of this class
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookingReferenceService {

    private static final String BOOKING_REFERENCE_PREFIX = "B_REF-";

    private final BookingReferenceRepository bookingReferenceRepository;

    // Bad Code
    public String generateBookingReferenceV1() {
        String bookingReference;
        do {
            bookingReference = generateRandomAlphanumericCode(10);
        } while (isBookingReferenceExists(bookingReference));

        return bookingReference;
    }

    // BAD CODE
    private String generateRandomAlphanumericCode(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
        Random random = new Random();
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            builder.append(characters.charAt(index));
        }

        return builder.toString();
    }

    private boolean isBookingReferenceExists(String bookingReference) {
        return bookingReferenceRepository.findByReferenceNumber(bookingReference).isPresent();
    }

    // This is the only valid code in here
    public void saveBookingReferenceToDB(String bookingReference) {
        BookingReference newBookingReference = BookingReference.builder()
                .referenceNumber(bookingReference)
                .build();

        bookingReferenceRepository.save(newBookingReference);
    }

    public String generateBookingReference() {
       return BOOKING_REFERENCE_PREFIX + UUID.randomUUID().toString()
               .replace("-", "");
    }
}
