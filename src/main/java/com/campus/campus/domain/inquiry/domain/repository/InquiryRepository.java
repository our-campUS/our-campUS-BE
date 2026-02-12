package com.campus.campus.domain.inquiry.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.campus.campus.domain.inquiry.domain.entity.Inquiry;
import com.campus.campus.domain.user.domain.entity.User;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
	Page<Inquiry> findAllByWriterOrderByCreatedAtDesc(User writer, Pageable pageable);
}
