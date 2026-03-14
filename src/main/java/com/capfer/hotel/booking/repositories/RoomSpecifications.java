package com.capfer.hotel.booking.repositories;

import com.capfer.hotel.booking.entities.Room;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class RoomSpecifications {

    public static Specification<Room> globalSearchV1(String searchTerm) {
        // cb stands for CriteriaBuilder, root is the root of the query (Room), and query is the CriteriaQuery
        return (root, query, cb) -> {
            if (!StringUtils.hasText(searchTerm)) {
                // Returns everything if search is empty
                return cb.conjunction(); // No filtering if search term is empty
            }

            String pattern = "%" + searchTerm.toLowerCase() + "%";
            List<Predicate> predicates = new ArrayList<>();

            // String fields case-insensitive search
            predicates.add(cb.like(cb.lower(root.get("roomNumber")), pattern));
            predicates.add(cb.like(cb.lower(root.get("roomType")), pattern));
            predicates.add(cb.like(cb.lower(root.get("description")), pattern));

            // Numeric fields (Cast to String first to allow "LIKE" search)
            // .as(String.class) generates the SQL CAST(... as VARCHAR)
            predicates.add(cb.like(root.get("pricePerNight").as(String.class), pattern));
            predicates.add(cb.like(root.get("capacity").as(String.class), pattern));

            // Combine with OR
            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Room> globalSearchV2(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(searchTerm)) {
                return criteriaBuilder.conjunction(); // No filtering if search term is empty
            }
            String likePattern = "%" + searchTerm.toLowerCase() + "%";
            return criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("roomNumber")), likePattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("roomType")), likePattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("pricePerNight").as(String.class)),likePattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("capacity").as(String.class)), likePattern)
            );
        };
    }

    // This is the most efficient way to search across multiple fields,
    // but it requires a GIN index on the searchVector column.
    public static Specification<Room> globalSearchV3(String searchParam) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(searchParam)) {
                return cb.conjunction();
            }
            // This matches the single GIN index perfectly
            return cb.like(
                    cb.lower(root.get("searchVector")), // Map this in your Entity!
                    "%" + searchParam.toLowerCase() + "%"
            );
        };
    }

}
