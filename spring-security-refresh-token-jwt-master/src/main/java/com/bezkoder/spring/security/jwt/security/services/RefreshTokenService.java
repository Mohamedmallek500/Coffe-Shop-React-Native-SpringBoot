package com.bezkoder.spring.security.jwt.security.services;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bezkoder.spring.security.jwt.exception.TokenRefreshException;
import com.bezkoder.spring.security.jwt.models.RefreshToken;
import com.bezkoder.spring.security.jwt.models.Utilisateur;
import com.bezkoder.spring.security.jwt.repository.RefreshTokenRepository;
import com.bezkoder.spring.security.jwt.repository.UtilisateurRepository;

@Service
public class RefreshTokenService {

  @Value("${bezkoder.app.jwtRefreshExpirationMs}")
  private Long refreshTokenDurationMs;

  @Autowired
  private RefreshTokenRepository refreshTokenRepository;

  @Autowired
  private UtilisateurRepository utilisateurRepository;

  public Optional<RefreshToken> findByToken(String token) {
    return refreshTokenRepository.findByToken(token);
  }

  public RefreshToken createRefreshToken(Long userId) {
    RefreshToken refreshToken = new RefreshToken();

    Utilisateur user = utilisateurRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur not found with id: " + userId));

    refreshToken.setUser(user);
    refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
    refreshToken.setToken(UUID.randomUUID().toString());

    return refreshTokenRepository.save(refreshToken);
  }

  public RefreshToken verifyExpiration(RefreshToken token) {
    if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
      refreshTokenRepository.delete(token);
      throw new TokenRefreshException(
              token.getToken(),
              "Refresh token was expired. Please make a new signin request"
      );
    }

    return token;
  }

  @Transactional
  public int deleteByUserId(Long userId) {
    Utilisateur user = utilisateurRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur not found with id: " + userId));

    return refreshTokenRepository.deleteByUser(user);
  }
}
