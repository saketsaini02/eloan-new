package com.iiht.training.eloan.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

public class UserDto {
	private Long id;
	@NotEmpty(message = "firstName is required!")
	@Length(max = 100, min = 3)
	private String firstName;
	@NotEmpty(message = "lastName is required!")
	@Length(max = 100, min = 3)
	private String lastName;
	@Email
	@Length(max = 100, min = 3)
	private String email;
	@Length(max = 10, min = 10)
	private String mobile;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	
}
