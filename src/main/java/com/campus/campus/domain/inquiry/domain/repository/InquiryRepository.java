package com.campus.campus.domain.inquiry.domain.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.inquiry.domain.entity.Inquiry;
import com.campus.campus.domain.inquiry.domain.entity.InquiryStatus;
import com.campus.campus.domain.inquiry.domain.entity.WriterType;
import com.campus.campus.domain.user.domain.entity.User;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
	Page<Inquiry> findAllByWriterOrderByCreatedAtDesc(User writer, Pageable pageable);

	Page<Inquiry> findAllByStudentCouncilWriterOrderByCreatedAtDesc(StudentCouncil council, Pageable pageable);

	@Query("""
		SELECT i FROM Inquiry i
		LEFT JOIN FETCH i.writer u
		LEFT JOIN FETCH i.studentCouncilWriter sc
		WHERE (:status IS NULL OR i.status = :status)
			AND (:writerType IS NULL OR i.writerType = :writerType)
		ORDER BY i.createdAt DESC
		""")
	List<Inquiry> findAllByCondition(
		@Param("status") InquiryStatus status,
		@Param("writerType") WriterType writerType
	);
}
