package com.example.karaoke.service;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.karaoke.model.Reservation;
import com.example.karaoke.model.User;
import com.example.karaoke.repository.ReservationRepository;
import com.example.karaoke.repository.UserRepository;

import jakarta.annotation.PostConstruct;

@Service
public class KaraokeService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    private static int currentReservationNumber = 1;
    
    @PostConstruct
    public void init() {
        List<Reservation> reservations = getAllReservations();
        if (!reservations.isEmpty()) {
            Reservation first = reservations.get(0);
            first.setNowPlaying(true);
            reservationRepository.save(first);
        }
    }
    
    public User loginUser(String username) {
        return userRepository.findById(username)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername(username);
                    newUser.setEveningCount(0);
                    return userRepository.save(newUser);
                });
    }
    
    public User getCurrentUser(String username) {
        return userRepository.findById(username).orElse(null);
    }

    private synchronized int getNextReservationNumber() {
        return currentReservationNumber++;
    }
    
    private void applySepiaEffect(BufferedImage img) {
        WritableRaster raster = img.getRaster();
        int width = img.getWidth();
        int height = img.getHeight();
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int[] pixels = raster.getPixel(x, y, (int[]) null);
                
                int r = pixels[0];
                int g = pixels[1];
                int b = pixels[2];
                
                int tr = (int) (0.393 * r + 0.769 * g + 0.189 * b);
                int tg = (int) (0.349 * r + 0.686 * g + 0.168 * b);
                int tb = (int) (0.272 * r + 0.534 * g + 0.131 * b);
                
                pixels[0] = Math.min(255, tr);
                pixels[1] = Math.min(255, tg);
                pixels[2] = Math.min(255, tb);
                
                raster.setPixel(x, y, pixels);
            }
        }
    }
    
    public Reservation makeReservation(String username, String song, MultipartFile photo) throws IOException {
        User user = userRepository.findById(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
    
        boolean alreadyParticipated = reservationRepository.existsByUsername(username);
        if (!alreadyParticipated) {
            int newCount = user.getEveningCount() + 1;
            if (newCount > 5) {
                newCount = 1;
            }
            user.setEveningCount(newCount);
            userRepository.save(user);
        }
    
        Reservation reservation = new Reservation();
        reservation.setUsername(username);
        reservation.setSong(song);
        reservation.setReservationNumber(getNextReservationNumber());
        reservation.setNowPlaying(false);
    
        if (photo != null && !photo.isEmpty()) {
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(photo.getBytes()));
            applySepiaEffect(originalImage);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(originalImage, "jpg", baos);
            byte[] imageData = baos.toByteArray();
            
            reservation.setPhotoData(imageData);
            reservation.setPhotoContentType("image/jpeg");
        }
    
        if (getAllReservations().isEmpty()) {
            reservation.setNowPlaying(true);
        }
    
        return reservationRepository.save(reservation);
    }
    
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAllByOrderByIdAsc();
    }
    
    public Reservation getReservation(Long id) {
        return reservationRepository.findById(id).orElse(null);
    }
    
    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
        updateNowPlaying();
    }
    
    public void closeEvening() {
        reservationRepository.deleteAll();
        currentReservationNumber = 1;
    }
    
    public void nextSong() {
        List<Reservation> reservations = getAllReservations();
        if (!reservations.isEmpty()) {
            Reservation current = reservations.get(0);
            reservationRepository.delete(current);
            updateNowPlaying();
        }
    }
    
    private void updateNowPlaying() {
        List<Reservation> reservations = getAllReservations();
        reservations.forEach(r -> r.setNowPlaying(false));
        if (!reservations.isEmpty()) {
            reservations.get(0).setNowPlaying(true);
            reservationRepository.saveAll(reservations);
        }
    }

    public boolean isUsernameTaken(String username) {
        return userRepository.findById(username).isPresent();
    }

    public void registerUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setEveningCount(0);
        userRepository.save(user);
    }
}