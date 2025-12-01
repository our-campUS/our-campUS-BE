package com.campus.campus.domain.school.domain.repository;

import com.campus.campus.domain.school.domain.entity.OcrImageType;
import com.campus.campus.domain.school.domain.entity.SchoolOcrImage;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolOcrImageRepository extends JpaRepository<SchoolOcrImage, Long> {

    Optional<SchoolOcrImage> findByUserIdAndObjectNameAndType(Long userId, String objectName, OcrImageType type);
    Optional<SchoolOcrImage> findByIdAndUserId(Long id, Long userId);

}
