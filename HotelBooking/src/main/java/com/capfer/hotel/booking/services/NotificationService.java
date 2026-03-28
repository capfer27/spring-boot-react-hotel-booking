package com.capfer.hotel.booking.services;

import com.capfer.hotel.booking.dtos.NotificationDTO;

public interface NotificationService {

    void sendEmail(NotificationDTO notificationDTO);

    void sendSMS();

    void sendWhatsapp();
}
