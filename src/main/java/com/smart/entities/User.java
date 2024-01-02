package com.smart.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="smartUser")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@NotBlank(message = "Name should not be blank !!! ")
	@Size(min = 3, max = 50, message = "Name between 3-50 character !!! ")
	private String name;
	@Column(unique = true)
	
	@Email(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" 
	        + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$" , message="Invalid email !!! ")
	private String email;
	
	// Regex to validate password: At least 8 characters, at least one uppercase letter,
    // at least one lowercase letter, at least one digit, and no whitespace
//	@NotBlank(message = "Password is Required !!! ")
//	@Email(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=\\S+$).{8,}$", message = "password must be 8 character and include special symbol !!! ")
//	private String password;
	
	
	@Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message = "Password must contain a special symbol")
    private String password;
	
	private String role;
	private boolean enabled;
	private String imageUrl;
	@Column(length = 1000)
	@NotBlank(message = "About message can't be Empty !!! ")
	private String about;
	
	
//	orphanRemoval = true [delete not working so i used this]
	@OneToMany(cascade = CascadeType.ALL,mappedBy = "user",orphanRemoval = true)
	private List<Contact> contacts=new ArrayList<>();
	
	public User() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

	@Override
	public boolean equals(Object obj) {
		return this.id==((Contact)obj).getcId();
	}
	
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", role=" + role
				+ ", enabled=" + enabled + ", imageUrl=" + imageUrl + ", about=" + about + ", contacts=" + contacts
				+ "]";
		
	}
	
	
	
}
