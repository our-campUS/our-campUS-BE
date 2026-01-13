package com.campus.campus.domain.stamp.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campus.campus.domain.stamp.domain.entity.Stamp;

public interface StampRepository extends JpaRepository<Stamp, Long> {
}
