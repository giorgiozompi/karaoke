package com.example.karaoke.repository;

import com.example.karaoke.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}