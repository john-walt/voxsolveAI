package com.b2b.AIhelper.entity;

import jakarta.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(unique = false)
    private String phoneNumber;

    @Column(unique = false, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;
    
    @Column(nullable = true)
    private String otp;
    
    
    @Column(nullable = true)
    private String name;

    public User() {}

    public User(String phoneNumber, String email, String password, String otp, String name) {
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.otp = otp;
        this.name = name;
        
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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
    	 BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    	    this.password = encoder.encode(password);
    }

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    
    
}
