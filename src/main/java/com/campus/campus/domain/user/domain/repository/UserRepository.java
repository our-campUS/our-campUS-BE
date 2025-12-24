package com.campus.campus.domain.user.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campus.campus.domain.user.domain.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByIdAndDeletedAtIsNull(Long userId);

	Optional<User> findByKakaoId(Long kakaoId);

	Optional<User> findByKakaoIdAndDeletedAtIsNull(Long kakaoId);

	boolean existsByIdAndDeletedAtIsNull(Long userId);

	List<User> findAllByDeletedAtIsNotNullAndDeletedAtBefore(LocalDateTime softDeleteDate);
}
