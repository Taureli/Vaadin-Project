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
	
	public Person(String nickname) {
		super();
		this.nickname = nickname;
	}

	public Person() {
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}
}
