package com.example.karaoke.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.karaoke.model.Reservation;
import com.example.karaoke.model.User;
import com.example.karaoke.service.KaraokeService;

import jakarta.servlet.http.HttpSession;

@Controller
public class KaraokeController {
    @Autowired
    private KaraokeService karaokeService;
    
    @GetMapping("/")
    public String homePage() {
        return "home";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username, Model model) {
        if (karaokeService.isUsernameTaken(username)) {
            model.addAttribute("error", "Username già esistente. Scegline un altro.");
            return "register";
        }
        karaokeService.registerUser(username);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam String username, HttpSession session, Model model) {
        if (!karaokeService.isUsernameTaken(username)) {
            model.addAttribute("error", "Username non trovato. Registrati prima.");
            return "login";
        }
        session.setAttribute("username", username);
        return "redirect:/list";
    }
    
    @GetMapping("/reserve")
    public String reservePage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/";
        }
        model.addAttribute("songs", List.of("883 - Ci Sono Anch'io",
"883 - Come Mai",
"883 - Hanno Ucciso l'Uomo Ragno",
"4 Non Blondes - What's Up",
"A-ha - Take On Me",
"Achille Lauro - 1969",
"Alessandro Di Traglia - Limbo",
"Anemone - Universo",
"Avril Lavigne - Complicated",
"Battisti - Con il Nastro Rosa",
"Baustelle - Charlie Fa Surf",
"Bon Jovi - It's My Life",
"Calcutta - Oroscopo",
"Caparezza - Vengo dalla Luna",
"Coldplay - Yellow",
"Cranberries - Just My Imagination",
"Cremonini - Marmellata#25",
"Cremonini - Stella di Broadway",
"Daft Punk - Get Lucky",
"De Gregori - Generale",
"De Gregori - La Donna Cannone",
"Elisa - Broken",
"Elisa - Luce",
"Eurythmics - Sweet Dreams",
"Foo Fighters - Learn to Fly",
"Gemelli Diversi - Mary",
"Gianna Nannini - America",
"Green Day - Basket Case",
"Grignani - Destinazione Paradiso",
"HIM - Wicked Game",
"Hole - Celebrity Skin",
"Kings of Leon - Sex on Fire",
"Kasabian - L.S.F.",
"Le Vibrazioni - Dedicato a Te",
"Ligabue - Piccola Stella Senza Cielo",
"Ligabue - Urlando Contro il Cielo",
"Linkin Park - In the End",
"Liquido - Narcotic",
"Litfiba - Il Mio Corpo che Cambia",
"Litfiba - Vivere il Mio Tempo",
"Loredana Bertè - È in Alto Mare",
"Lynyrd Skynyrd - Simple Man",
"Marilyn Manson - Tainted Love",
"Natalie Imbruglia - Torn",
"Negrita - Hemingway",
"Negrita - Ho Imparato a Sognare",
"Negrita - Sex",
"Oasis - Morning Glory",
"Oasis - Slide Away",
"One Piece - All'arrembaggio!",
"P!nk - Try",
"Paolo Nutini - Candy",
"Placebo - Every You Every Me",
"Prozac+ - Acida",
"Radiohead - Creep",
"Ramones - Blitzkrieg Bop",
"Robbie Williams - Feel",
"Sum 41 - Pieces",
"The Calling - Wherever You Will Go",
"The Rasmus - In the Shadows",
"Tiziano Ferro - Sere Nere",
"Tom Petty - Love is a Long Road",
"Vasco - Albachiara",
"Vasco - Rewind",
"Vasco - T'immagini",
"Verdena - Valvonauta",
"Zero Assoluto - Per Dimenticare",
"Zucchero - Diavolo in Me"));
        return "reserve";
    }
    
    @PostMapping("/reserve")
    public String makeReservation(
            @RequestParam String song,
            @RequestParam(required = false) MultipartFile photo,
            HttpSession session) throws IOException {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/";
        }
        karaokeService.makeReservation(username, song, photo);
        return "redirect:/list";
    }
    
    @GetMapping("/list")
    public String listReservations(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username != null) {
            User currentUser = karaokeService.getCurrentUser(username);
            model.addAttribute("currentUser", currentUser);
        }
        model.addAttribute("reservations", karaokeService.getAllReservations());
        return "list";
    }
    
    @GetMapping("/image/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        Reservation reservation = karaokeService.getReservation(id);
        if (reservation != null && reservation.getPhotoData() != null) {
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(reservation.getPhotoContentType()))
                .body(reservation.getPhotoData());
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/admin")
    public String adminPage(Model model) {
        model.addAttribute("reservations", karaokeService.getAllReservations());
        return "admin";
    }
    
    @PostMapping("/admin/delete/{id}")
    public String deleteReservation(@PathVariable Long id) {
        karaokeService.deleteReservation(id);
        return "redirect:/admin";
    }
    
    @PostMapping("/admin/close")
    public String closeEvening() {
        karaokeService.closeEvening();
        return "redirect:/admin";
    }
    
    @PostMapping("/admin/next")
    public String nextSong() {
        karaokeService.nextSong();
        return "redirect:/admin";
    }
}