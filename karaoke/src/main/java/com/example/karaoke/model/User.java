package com.example.karaoke.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    private String username;
    private int eveningCount;
}