package com.hotel.guest_service.repository;

import com.hotel.guest_service.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Long> {
    Optional<Guest> findByEmail(String email);
    Optional<Guest> findByMemberCode(String memberCode);
    boolean existsByEmail(String email);
}