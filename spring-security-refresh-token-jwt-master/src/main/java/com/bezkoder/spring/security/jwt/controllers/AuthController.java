package com.bezkoder.spring.security.jwt.controllers;

import java.util.List;
import java.util.stream.Collectors;

import com.bezkoder.spring.security.jwt.models.*;
import com.bezkoder.spring.security.jwt.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.bezkoder.spring.security.jwt.exception.TokenRefreshException;
import com.bezkoder.spring.security.jwt.payload.request.LoginRequest;
import com.bezkoder.spring.security.jwt.payload.request.SignupRequest;
import com.bezkoder.spring.security.jwt.payload.response.UserInfoResponse;
import com.bezkoder.spring.security.jwt.payload.response.MessageResponse;
import com.bezkoder.spring.security.jwt.repository.RoleRepository;
import com.bezkoder.spring.security.jwt.repository.UtilisateurRepository;
import com.bezkoder.spring.security.jwt.security.jwt.JwtUtils;
import com.bezkoder.spring.security.jwt.security.services.RefreshTokenService;
import com.bezkoder.spring.security.jwt.security.services.UserDetailsImpl;
import org.springframework.web.multipart.MultipartFile;


@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private UtilisateurRepository utilisateurRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private PasswordEncoder encoder;

  @Autowired
  private JwtUtils jwtUtils;

  @Autowired
  private RefreshTokenService refreshTokenService;

  @Autowired
  private FileStorageService fileStorageService;

  // ===========================
  //            SIGNIN
  // ===========================
  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
            )
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    Utilisateur user = utilisateurRepository
            .findByEmail(userDetails.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

    ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

    RefreshToken refreshToken =
            refreshTokenService.createRefreshToken(userDetails.getId());

    ResponseCookie refreshCookie =
            jwtUtils.generateRefreshJwtCookie(refreshToken.getToken());

    List<String> roles = userDetails.getAuthorities()
            .stream()
            .map(a -> a.getAuthority())
            .toList();

    UserInfoResponse response = new UserInfoResponse(
            user.getId(),
            user.getNom(),
            user.getPrenom(),
            user.getEmail(),
            user.getPhoto(), // image renvoyée
            roles
    );

    return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(response);
  }

  // ===========================
  //            SIGNUP (AVEC IMAGE)
  // ===========================
  @PostMapping(value = "/signup", consumes = "multipart/form-data")
  public ResponseEntity<?> registerUser(
          @RequestParam("nom") String nom,
          @RequestParam("prenom") String prenom,
          @RequestParam("email") String email,
          @RequestParam("password") String password,
          @RequestParam("telephone") String telephone,
          @RequestParam("cin") String cin,
          @RequestParam("role") ERole role,
          @RequestParam(value = "photo", required = false) MultipartFile photo
  ) {

    if (utilisateurRepository.existsByEmail(email)) {
      return ResponseEntity.badRequest()
              .body(new MessageResponse("Email already in use"));
    }

    Utilisateur user;

    switch (role) {
      case ADMIN -> user = new Admin();
      case CLIENT -> user = new Client();
      default -> throw new RuntimeException("Unsupported role");
    }

    user.setNom(nom);
    user.setPrenom(prenom);
    user.setEmail(email);
    user.setMotDePasse(encoder.encode(password));
    user.setTelephone(telephone);
    user.setCin(cin);

    // 🔥 UPLOAD IMAGE
    if (photo != null && !photo.isEmpty()) {
      String fileName = fileStorageService.saveFile(photo);
      user.setPhoto(fileName);
    }

    Role userRole = roleRepository.findByName(role)
            .orElseThrow(() -> new RuntimeException("Role not found"));

    user.setRole(userRole);

    utilisateurRepository.save(user);

    return ResponseEntity.ok(
            new MessageResponse("User registered successfully")
    );
  }

  // ===========================
  //      UPDATE PHOTO PROFIL
  // ===========================
  @PutMapping(value = "/users/{id}/photo", consumes = "multipart/form-data")
  public ResponseEntity<?> updatePhoto(
          @PathVariable Long id,
          @RequestParam("photo") MultipartFile photo
  ) {

    Utilisateur user = utilisateurRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));

    if (photo == null || photo.isEmpty()) {
      return ResponseEntity.badRequest()
              .body(new MessageResponse("Aucune image envoyée"));
    }

    String fileName = fileStorageService.saveFile(photo);
    user.setPhoto(fileName);

    utilisateurRepository.save(user);

    return ResponseEntity.ok(new MessageResponse("Photo mise à jour"));
  }
}
