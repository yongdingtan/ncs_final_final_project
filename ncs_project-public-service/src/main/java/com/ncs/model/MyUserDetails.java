package com.ncs.model;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class MyUserDetails implements UserDetails {

	private int id;

	private String username;

	private String email;
	@JsonIgnore
	private String password;

	private String role;

	private Collection<? extends GrantedAuthority> authorities;

	public MyUserDetails(int id, String username, String email, String password, String role) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
		this.role = role;
	}

	public MyUserDetails(User user) {
		// TODO Auto-generated constructor stub
	}

	public static MyUserDetails build(User user) {

		return new MyUserDetails(user.getUserId(), user.getUsername(), user.getEmail(), user.getPassword(),
				user.getRole());
	}

	public String getEmail() {
		return email;
	}

	public String getRole() {
		return role;
	}

	public int getId() {
		return id;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return authorities;
	}

}
