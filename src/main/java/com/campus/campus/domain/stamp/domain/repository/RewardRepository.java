package com.campus.campus.domain.stamp.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campus.campus.domain.stamp.domain.entity.Reward;
import com.campus.campus.domain.user.domain.entity.User;

public interface RewardRepository extends JpaRepository<Reward, Long> {
	List<Reward> findAllByUserOrderByRewardIdDesc(User user);
}
