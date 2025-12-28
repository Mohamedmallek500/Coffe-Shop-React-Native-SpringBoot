package com.bezkoder.spring.security.jwt.payload.response;

import java.util.List;

public class UserInfoResponse {

	private Long id;
	private String nom;
	private String prenom;
	private String email;
	private String photo;
	private List<String> roles;

	public UserInfoResponse(
			Long id,
			String nom,
			String prenom,
			String email,
			String photo,
			List<String> roles
	) {
		this.id = id;
		this.nom = nom;
		this.prenom = prenom;
		this.email = email;
		this.photo = photo;
		this.roles = roles;
	}

	public Long getId() { return id; }
	public String getNom() { return nom; }
	public String getPrenom() { return prenom; }
	public String getEmail() { return email; }
	public String getPhoto() { return photo; }
	public List<String> getRoles() { return roles; }
}
