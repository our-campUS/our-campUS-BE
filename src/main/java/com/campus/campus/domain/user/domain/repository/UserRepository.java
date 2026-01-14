package com.campus.campus.domain.user.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campus.campus.domain.user.domain.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByIdAndDeletedAtIsNull(Long userId);

	Optional<User> findByKakaoId(Long kakaoId);

	Optional<User> findByKakaoIdAndDeletedAtIsNull(Long kakaoId);

	boolean existsByIdAndDeletedAtIsNull(Long userId);

	boolean existsByCampusNicknameAndIdNot(String campusNickname, Long userId);

	List<User> findAllByDeletedAtIsNotNullAndDeletedAtBefore(LocalDateTime softDeleteDate);

	@Query("""
       SELECT u FROM User u
       LEFT JOIN FETCH u.school
       LEFT JOIN FETCH u.college
       LEFT JOIN FETCH u.major
       WHERE u.id = :userId
       AND u.deletedAt IS NULL
       """)
	Optional<User> findByIdWithAcademicInfo(@Param("userId") Long userId);

	List<User> findAllByMajor_MajorIdAndDeletedAtIsNull(Long majorId);

	List<User> findAllByCollege_CollegeIdAndDeletedAtIsNull(Long collegeId);

	List<User> findAllBySchool_SchoolIdAndDeletedAtIsNull(Long schoolId);

}
