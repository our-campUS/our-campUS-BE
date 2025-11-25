package com.campus.campus.global.util.jwt;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.campus.campus.domain.council.domain.entity.CouncilType;
import com.campus.campus.domain.council.domain.entity.StudentCouncil;

import lombok.Getter;

@Getter
public class StudentCouncilPrincipal implements UserDetails {
	private final Long councilId;
	private final String username; // 로그인 아이디 (이메일)
	private final CouncilType councilType;
	private final List<GrantedAuthority> authorities;

	private StudentCouncilPrincipal(
		Long councilId,
		String username,
		CouncilType councilType,
		List<GrantedAuthority> authorities
	) {
		this.councilId = councilId;
		this.username = username;
		this.councilType = councilType;
		this.authorities = authorities;
	}

	public static StudentCouncilPrincipal from(StudentCouncil studentCouncil) {
		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_COUNCIL"));
		return new StudentCouncilPrincipal(
			studentCouncil.getId(),
			studentCouncil.getLoginId(),
			studentCouncil.getCouncilType(),
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
