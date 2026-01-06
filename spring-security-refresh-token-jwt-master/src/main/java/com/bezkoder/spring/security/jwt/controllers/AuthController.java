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

    ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

    RefreshToken refreshToken =
            refreshTokenService.createRefreshToken(userDetails.getId());

    ResponseCookie refreshCookie =
            jwtUtils.generateRefreshJwtCookie(refreshToken.getToken());

    List<String> roles = userDetails.getAuthorities()
            .stream()
            .map(a -> a.getAuthority())
            .toList();

    // 🔥 Réponse avec TOUTES les infos
    UserInfoResponse response = new UserInfoResponse(
            userDetails.getId(),
            userDetails.getNom(),
            userDetails.getPrenom(),
            userDetails.getEmail(),
            userDetails.getPhoto(),
            userDetails.getTelephone(),
            userDetails.getCin(),
            roles
    );

    return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(response);
  }
}
