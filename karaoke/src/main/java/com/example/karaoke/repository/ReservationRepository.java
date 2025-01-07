package com.example.karaoke.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.karaoke.model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByOrderByIdAsc();
    boolean existsByUsername(String username);
}