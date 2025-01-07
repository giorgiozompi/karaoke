package com.example.karaoke.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    private String song;
    
    @Lob
    @Column(length = 1000000)
    private byte[] photoData;
    
    private String photoContentType;
    private int reservationNumber;
    private boolean nowPlaying;
    
    @ManyToOne
    @JoinColumn(name = "username", insertable = false, updatable = false)
    private User user;
}