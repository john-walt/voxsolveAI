package com.b2b.AIhelper.models;

public class SignupRequest {
    private String phoneNumber;
    
    private String email;
    
    private String password;
    
    private String name;

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
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    
    
}
