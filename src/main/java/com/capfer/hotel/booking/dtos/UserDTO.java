package com.capfer.hotel.booking.dtos;

import com.capfer.hotel.booking.entities.Booking;
import com.capfer.hotel.booking.enums.RoomType;
import com.capfer.hotel.booking.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {

    private Long id;

    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private UserRole role; // CUSTOMER, ADMIN, STAFF, etc.

    private Boolean isActive;

    private LocalDateTime createdAt;
}
