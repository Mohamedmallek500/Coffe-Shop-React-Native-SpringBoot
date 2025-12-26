package com.bezkoder.spring.security.jwt.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "admins")
public class Admin extends Utilisateur {

    // Pour l'instant, pas de champs spécifiques.
    // Tu pourras ajouter d'autres attributs plus tard (ex: grade, bureau, etc.)
}
