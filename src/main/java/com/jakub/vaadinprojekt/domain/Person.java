package com.jakub.vaadinprojekt.domain;

import java.io.Serializable;
import java.util.UUID;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Person implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private UUID id;
	
	@NotNull
	@Size(min=3, max=50)
	private String nickname;
	
//	@NotNull
//	@Size(min=6, max=50)
//	private String password;
//	
//	@NotNull
//	private String passwordTest;
	
	public Person(String nickname) {
		super();
		this.nickname = nickname;
//		this.password = password;
//		this.passwordTest = passwordTest;
	}

	public Person() {
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
//	
//	public String getPassword() {
//		return password;
//	}
//
//	public void setPassword(String password) {
//		this.password = password;
//	}
//	
//	public String getPasswordTest() {
//		return passwordTest;
//	}
//
//	public void setPasswordTest(String passwordTest) {
//		this.passwordTest = passwordTest;
//	}
//	
//	@AssertTrue(message="Passwords don't match")
//	 public boolean isValid() {
//	    return this.password.equals(this.passwordTest);
//	 }

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}
}
