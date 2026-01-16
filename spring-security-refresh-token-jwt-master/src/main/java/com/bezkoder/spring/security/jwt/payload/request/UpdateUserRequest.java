package com.bezkoder.spring.security.jwt.payload.request;

public class UpdateUserRequest {

    private String nom;
    private String prenom;
    private String telephone;
    private String photo;
    private String cin;
    private String password;   // <-- ajout

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public String getCin() { return cin; }
    public void setCin(String cin) { this.cin = cin; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
