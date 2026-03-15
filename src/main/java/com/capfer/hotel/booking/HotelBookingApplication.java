package com.capfer.hotel.booking;

import com.capfer.hotel.booking.dtos.NotificationDTO;
import com.capfer.hotel.booking.enums.NotificationType;
import com.capfer.hotel.booking.services.NotificationService;
import io.micrometer.observation.Observation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.UUID;

@SpringBootApplication
@EnableAsync
public class HotelBookingApplication
//		implements CommandLineRunner
{

	@Autowired
	private NotificationService notificationService;

	static void main(String[] args) {
		SpringApplication.run(HotelBookingApplication.class, args);
	}

	/**
	 * Callback used to run the bean.
	 *
	 * @param args incoming main method arguments
	 * @throws Exception on error
	 */
//	@Override
//	public void run(String... args) throws Exception {
//		NotificationDTO notificationDTO = NotificationDTO.builder()
//				.notificationType(NotificationType.EMAIL)
//				.bookingReference(UUID.randomUUID().toString())
//				.recipient("capfer27@gmail.com")
//				.subject("Test Email")
//				.body("This is a test email from the Hotel Booking Application. Enjoy your stay! 😊")
//				.build();
//		notificationService.sendEmail(notificationDTO);
//	}
}
