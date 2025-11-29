package com.campus.campus.domain.user.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campus.campus.domain.user.domain.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByKakaoId(Long kakaoId);
}
