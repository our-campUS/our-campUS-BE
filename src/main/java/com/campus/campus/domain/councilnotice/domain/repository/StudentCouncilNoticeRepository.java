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
		"LEFT JOIN FETCH w.school " +
		"LEFT JOIN FETCH w.college " +
		"LEFT JOIN FETCH w.major " +
		"WHERE n.id = :noticeId")
	Optional<StudentCouncilNotice> findByIdWithFullInfo(@Param("noticeId") Long noticeId);

	@Query("SELECT n FROM StudentCouncilNotice n " +
		"JOIN FETCH n.writer " +
		"WHERE n.id IN (" +
		"   SELECT notice.id FROM StudentCouncilNotice notice " +
		"   ORDER BY notice.createdAt DESC" +
		")")
	Page<StudentCouncilNotice> findAllWithWriter(Pageable pageable);
}
