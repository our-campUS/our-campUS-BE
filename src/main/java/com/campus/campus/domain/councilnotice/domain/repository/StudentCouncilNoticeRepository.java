package com.campus.campus.domain.councilnotice.domain.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campus.campus.domain.councilnotice.domain.entity.StudentCouncilNotice;

public interface StudentCouncilNoticeRepository extends JpaRepository<StudentCouncilNotice, Long> {

	@Query("SELECT n FROM StudentCouncilNotice n " +
		"JOIN FETCH n.writer w " +
		"JOIN FETCH w.school " +
		"LEFT JOIN FETCH w.college " +
		"LEFT JOIN FETCH w.major " +
		"WHERE n.id = :noticeId")
	Optional<StudentCouncilNotice> findByIdWithFullInfo(@Param("noticeId") Long noticeId);

	@Query(value= "SELECT n FROM StudentCouncilNotice n " +
		"JOIN FETCH n.writer " ,
		countQuery = "SELECT COUNT(n) FROM StudentCouncilNotice n")
	Page<StudentCouncilNotice> findAllWithWriter(Pageable pageable);
}
