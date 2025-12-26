package com.bezkoder.spring.security.jwt.security.services;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.bezkoder.spring.security.jwt.models.Utilisateur;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserDetailsImpl implements UserDetails {

  private static final long serialVersionUID = 1L;

  private Long id;
  private String nom;
  private String prenom;
  private String email;

  @JsonIgnore
  private String motDePasse;

  private Collection<? extends GrantedAuthority> authorities;

  public UserDetailsImpl(
          Long id,
          String nom,
          String prenom,
          String email,
          String motDePasse,
          Collection<? extends GrantedAuthority> authorities) {

    this.id = id;
    this.nom = nom;
    this.prenom = prenom;
    this.email = email;
    this.motDePasse = motDePasse;
    this.authorities = authorities;
  }

  // ============================
  //        FIX ROLE
  // ============================
  public static UserDetailsImpl build(Utilisateur user) {

    // 🔥 Correction : récupérer l'ENUM du rôle
    String roleName = user.getRole().getName().name(); // ex : "ETUDIANT"

    // 🔥 Spring Security lit les rôles via GrantedAuthority
    List<GrantedAuthority> authorities =
            List.of(new SimpleGrantedAuthority(roleName));

    return new UserDetailsImpl(
            user.getId(),
            user.getNom(),
            user.getPrenom(),
            user.getEmail(),
            user.getMotDePasse(),
            authorities
    );
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  public Long getId() { return id; }
  public String getNom() { return nom; }
  public String getPrenom() { return prenom; }
  public String getEmail() { return email; }

  @Override
  public String getPassword() {
    return motDePasse;
  }

  @Override
  public String getUsername() {
    return email; // identifiant = email
  }

  @Override public boolean isAccountNonExpired() { return true; }
  @Override public boolean isAccountNonLocked() { return true; }
  @Override public boolean isCredentialsNonExpired() { return true; }
  @Override public boolean isEnabled() { return true; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserDetailsImpl user = (UserDetailsImpl) o;
    return Objects.equals(id, user.id);
  }
}
