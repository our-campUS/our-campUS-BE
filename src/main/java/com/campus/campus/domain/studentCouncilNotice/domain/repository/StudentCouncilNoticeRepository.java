package com.campus.campus.domain.studentCouncilNotice.domain.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campus.campus.domain.studentCouncilNotice.domain.entity.StudentCouncilNotice;

public interface StudentCouncilNoticeRepository extends JpaRepository<StudentCouncilNotice, Long> {

	@Query("SELECT n FROM StudentCouncilNotice n " +
		"JOIN FETCH n.writer w " +
		"LEFT JOIN FETCH w.school " +
		"LEFT JOIN FETCH w.college " +
		"LEFT JOIN FETCH w.major " +
		"WHERE n.id = :noticeId")
	Optional<StudentCouncilNotice> findByIdWithFullInfo(@Param("noticeId") Long noticeId);

	@Query("SELECT n FROM StudentCouncilNotice n " +
		"JOIN FETCH n.writer w " +
		"WHERE n.id = :noticeId")
	Optional<StudentCouncilNotice> findByIdWithWriter(@Param("noticeId") Long noticeId);

	Page<StudentCouncilNotice> findAll(Pageable pageable);
}
