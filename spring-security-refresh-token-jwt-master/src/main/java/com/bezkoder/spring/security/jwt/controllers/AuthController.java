package com.bezkoder.spring.security.jwt.controllers;

import java.util.List;
import java.util.stream.Collectors;

import com.bezkoder.spring.security.jwt.models.*;
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

    // 🔥 récupérer l'utilisateur complet depuis la base
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
            user.getPhoto(), // ✅ IMAGE RENVOYÉE
            roles
    );

    return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(response);
  }


  // ===========================
  //            SIGNUP
  // ===========================
  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest request) {

    if (utilisateurRepository.existsByEmail(request.getEmail())) {
      return ResponseEntity.badRequest()
              .body(new MessageResponse("Email already in use"));
    }

    Utilisateur user;

    switch (request.getRole()) {
      case ADMIN -> user = new Admin();
      case CLIENT -> user = new Client();
      default -> throw new RuntimeException("Unsupported role");
    }

    user.setNom(request.getNom());
    user.setPrenom(request.getPrenom());
    user.setEmail(request.getEmail());
    user.setMotDePasse(encoder.encode(request.getPassword()));
    user.setTelephone(request.getTelephone());
    user.setCin(request.getCin());
    user.setPhoto(request.getPhoto());

    Role role = roleRepository.findByName(request.getRole())
            .orElseThrow(() -> new RuntimeException("Role not found"));

    user.setRole(role);

    utilisateurRepository.save(user);

    return ResponseEntity.ok(
            new MessageResponse("User registered successfully")
    );
  }

  // ===========================
  //            LOGOUT
  // ===========================
  @PostMapping("/signout")
  public ResponseEntity<?> logoutUser() {

    Object principal =
            SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();

    if (principal instanceof UserDetailsImpl userDetails) {
      refreshTokenService.deleteByUserId(userDetails.getId());
    }

    return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, jwtUtils.getCleanJwtCookie().toString())
            .header(HttpHeaders.SET_COOKIE, jwtUtils.getCleanJwtRefreshCookie().toString())
            .body(new MessageResponse("Logged out"));
  }

  // ===========================
  //        REFRESH TOKEN
  // ===========================
  @PostMapping("/refreshtoken")
  public ResponseEntity<?> refreshToken(HttpServletRequest request) {

    String refreshToken =
            jwtUtils.getJwtRefreshFromCookies(request);

    if (refreshToken == null) {
      return ResponseEntity.badRequest()
              .body(new MessageResponse("Refresh token missing"));
    }

    return refreshTokenService.findByToken(refreshToken)
            .map(refreshTokenService::verifyExpiration)
            .map(RefreshToken::getUser)
            .map(user -> {
              ResponseCookie jwtCookie =
                      jwtUtils.generateJwtCookie(user);
              return ResponseEntity.ok()
                      .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                      .body(new MessageResponse("Token refreshed"));
            })
            .orElseThrow(() ->
                    new TokenRefreshException(
                            refreshToken,
                            "Refresh token not found"
                    )
            );
  }
}
