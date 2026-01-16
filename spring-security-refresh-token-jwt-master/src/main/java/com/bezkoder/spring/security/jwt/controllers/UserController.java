package com.bezkoder.spring.security.jwt.controllers;

import com.bezkoder.spring.security.jwt.payload.response.UserInfoResponse;
import com.bezkoder.spring.security.jwt.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.bezkoder.spring.security.jwt.models.Utilisateur;
import com.bezkoder.spring.security.jwt.payload.request.UpdateUserRequest;
import com.bezkoder.spring.security.jwt.repository.UtilisateurRepository;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FileStorageService fileStorageService; // 👈 AJOUT

    // ===========================
    // UPDATE PROFIL (JSON)
    // ===========================
    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(
            Authentication authentication,
            @RequestBody UpdateUserRequest request) {

        String email = authentication.getName();

        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // ===========================
        // VÉRIF CIN
        // ===========================
        if (request.getCin() != null &&
                !request.getCin().equals(user.getCin()) &&
                utilisateurRepository.existsByCin(request.getCin())) {

            return ResponseEntity
                    .badRequest()
                    .body("CIN déjà utilisé par un autre utilisateur");
        }

        // ===========================
        // VÉRIF TÉLÉPHONE
        // ===========================
        if (request.getTelephone() != null &&
                !request.getTelephone().equals(user.getTelephone()) &&
                utilisateurRepository.existsByTelephone(request.getTelephone())) {

            return ResponseEntity
                    .badRequest()
                    .body("Numéro de téléphone déjà utilisé");
        }

        // ===========================
        // MISE À JOUR DES CHAMPS
        // ===========================
        if (request.getNom() != null)
            user.setNom(request.getNom());

        if (request.getPrenom() != null)
            user.setPrenom(request.getPrenom());

        if (request.getTelephone() != null)
            user.setTelephone(request.getTelephone());

        if (request.getCin() != null)
            user.setCin(request.getCin());

        // ===========================
        // MODIFICATION MOT DE PASSE
        // ===========================
        if (request.getPassword() != null && !request.getPassword().isBlank()) {

            if (request.getPassword().length() < 6) {
                return ResponseEntity
                        .badRequest()
                        .body("Mot de passe trop court (min 6 caractères)");
            }

            String encodedPassword = passwordEncoder.encode(request.getPassword());
            user.setMotDePasse(encodedPassword);
        }

        utilisateurRepository.save(user);

        return ResponseEntity.ok(new UserInfoResponse(
                user.getId(),
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getPhoto(),
                user.getTelephone(),
                user.getCin(),
                List.of(user.getRole().getName().name())));
    }

    // ===========================
    // UPDATE PHOTO (MULTIPART)
    // ===========================
    @PutMapping(value = "/me/photo", consumes = "multipart/form-data")
    public ResponseEntity<?> updateMyPhoto(
            Authentication authentication,
            @RequestParam("photo") MultipartFile photo) {

        String email = authentication.getName();

        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (photo == null || photo.isEmpty()) {
            return ResponseEntity.badRequest().body("Fichier photo manquant");
        }

        try {
            // 🔥 Sauvegarde réelle du fichier
            String fileName = fileStorageService.saveFile(photo);
            user.setPhoto(fileName);

            utilisateurRepository.save(user);

            return ResponseEntity.ok(new UserInfoResponse(
                    user.getId(),
                    user.getNom(),
                    user.getPrenom(),
                    user.getEmail(),
                    user.getPhoto(),
                    user.getTelephone(),
                    user.getCin(),
                    List.of(user.getRole().getName().name())));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur lors de l'upload de la photo");
        }
    }
}
