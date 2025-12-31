package com.campus.campus.domain.councilNotice.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campus.campus.domain.councilNotice.domain.entity.NoticeImage;
import com.campus.campus.domain.councilNotice.domain.entity.StudentCouncilNotice;

public interface NoticeImageRepository extends JpaRepository<NoticeImage, Long> {

	List<NoticeImage> findAllByNotice(StudentCouncilNotice notice);

	List<NoticeImage> findAllByNoticeOrderByIdAsc(StudentCouncilNotice notice);

	void deleteByNotice(StudentCouncilNotice notice);
}
