package com.campus.campus.domain.inquiry.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.campus.campus.domain.inquiry.domain.entity.Inquiry;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
}
