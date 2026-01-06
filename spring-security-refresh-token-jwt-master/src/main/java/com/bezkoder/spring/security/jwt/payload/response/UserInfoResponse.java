package com.bezkoder.spring.security.jwt.payload.response;

import java.util.List;

public class UserInfoResponse {

	private Long id;
	private String nom;
	private String prenom;
	private String email;
	private String photo;
	private String telephone;
	private String cin;
	private List<String> roles;

	public UserInfoResponse(
			Long id,
			String nom,
			String prenom,
			String email,
			String photo,
			String telephone,
			String cin,
			List<String> roles
	) {
		this.id = id;
		this.nom = nom;
		this.prenom = prenom;
		this.email = email;
		this.photo = photo;
		this.telephone = telephone;
		this.cin = cin;
		this.roles = roles;
	}

	public Long getId() { return id; }
	public String getNom() { return nom; }
	public String getPrenom() { return prenom; }
	public String getEmail() { return email; }
	public String getPhoto() { return photo; }
	public String getTelephone() { return telephone; }
	public String getCin() { return cin; }
	public List<String> getRoles() { return roles; }
}
