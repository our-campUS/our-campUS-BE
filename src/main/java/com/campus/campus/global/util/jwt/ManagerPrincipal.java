package com.campus.campus.global.util.jwt;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.campus.campus.domain.manager.domain.entity.Manager;

import lombok.Getter;

@Getter
public class ManagerPrincipal implements UserDetails {
	private final Long managerId;
	private final String managerName;
	private final List<GrantedAuthority> authorities;

	private ManagerPrincipal(
		Long managerId,
		String managerName,
		List<GrantedAuthority> authorities
	) {
		this.managerId = managerId;
		this.managerName = managerName;
		this.authorities = authorities;
	}

	public static ManagerPrincipal from(Manager manager) {
		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_MANAGER"));
		return new ManagerPrincipal(
			manager.getId(),
			manager.getManagerName(),
			authorities
		);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return null; // 인증에서 비밀번호는 안 씀
	}

	@Override
	public String getUsername() {
		return managerName;
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
