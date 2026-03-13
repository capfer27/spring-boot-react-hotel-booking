package com.capfer.hotel.booking.entities;

import com.capfer.hotel.booking.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Email is required")
    @Column(name = "email",unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Password is required")
    @Column(name = "password", nullable = false)
    private String password;


    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "lastName", nullable = false)
    private String lastName;

    @NotBlank(message = "Phone number is required")
    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;

    private Boolean isActive;
    private final LocalDateTime createdAt = LocalDateTime.now();

    // mappedBy points to the field name in the Booking class
    // orphanRemoval ensures that if a booking is removed from the list, it's deleted from DB
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();

    // Helper method to keep both sides of the relationship in sync
    public void addBooking(Booking booking) {
        bookings.add(booking);
        booking.setUser(this);
    }

    //In most financial modules, you should keep the PaymentEntity as the "owner" of the relationship.
    //Avoid putting List<Payment> inside the User entity unless you frequently need to display
    // a user's entire billing history every time you load their profile.
    //Why? Over years, a user may have hundreds of payment attempts (succeeded, failed, refunded).
    // Loading this list automatically is a performance killer.
    // Keep this LAZY. Never make a payment list EAGER.
    //@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    //private List<PaymentEntity> payments = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(email, user.email) && Objects.equals(password, user.password) && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(phoneNumber, user.phoneNumber) && role == user.role && Objects.equals(isActive, user.isActive) && Objects.equals(createdAt, user.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password, firstName, lastName, phoneNumber, role, isActive, createdAt);
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", role=" + role +
                ", isActive=" + isActive +
                '}';
    }
}
