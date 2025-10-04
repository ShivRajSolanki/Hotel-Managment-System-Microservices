package com.hotel_management.authentication_service.repository;

import com.hotel_management.authentication_service.entity.Staff;
import com.hotel_management.authentication_service.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    Optional<Staff> findByUsername(String username);

    List<Staff> findByRole(Role role);
}

