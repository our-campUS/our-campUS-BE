package com.campus.campus.global.util.jwt;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.campus.campus.domain.user.domain.entity.User;

import lombok.Getter;

@Getter
public class UserPrincipal implements UserDetails {
	private final Long userId;
	private final String username;
	private final String email;
	private final String profileImage;
	private final List<GrantedAuthority> authorities;

	private UserPrincipal(Long userId, String username, String email, String profileImage,
		List<GrantedAuthority> authorities) {
		this.userId = userId;
		this.username = username;
		this.email = email;
		this.profileImage = profileImage;
		this.authorities = authorities;
	}

	public static UserPrincipal from(User user) {
		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

		return new UserPrincipal(
			user.getId(),
			user.getNickname(),
			user.getEmail(),
			user.getProfileImage(),
			authorities
		);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return null;
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
}
