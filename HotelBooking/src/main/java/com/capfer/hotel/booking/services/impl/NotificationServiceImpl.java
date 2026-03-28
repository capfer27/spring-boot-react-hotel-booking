package com.capfer.hotel.booking.services.impl;

import com.capfer.hotel.booking.dtos.NotificationDTO;
import com.capfer.hotel.booking.entities.Notification;
import com.capfer.hotel.booking.enums.NotificationType;
import com.capfer.hotel.booking.repositories.NotificationRepository;
import com.capfer.hotel.booking.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final ModelMapper modelMapper;

    private final JavaMailSender mailSender;

    private final NotificationRepository notificationRepository;

    @Override
    @Async
    public void sendEmail(NotificationDTO notificationDTO) {
        log.info("Preparing to send email to: {}", notificationDTO.getRecipient());

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(notificationDTO.getRecipient());
        mailMessage.setSubject(notificationDTO.getSubject());
        mailMessage.setText(notificationDTO.getBody());

        mailSender.send(mailMessage);

        // Save the notification to the database
        Notification notificationToSave = Notification.builder()
                .recipient(notificationDTO.getRecipient())
                .subject(notificationDTO.getSubject())
                .body(notificationDTO.getBody())
                .bookingReference(notificationDTO.getBookingReference())
                .notificationType(NotificationType.EMAIL)
                .build();

        Notification saved = notificationRepository.save(notificationToSave);

        log.info("Email notification sent successfully to: {}", notificationDTO.getRecipient());

    }

    @Override
    public void sendSMS() {

    }

    @Override
    public void sendWhatsapp() {

    }
}
