package com.campus.campus.domain.councilpost.application;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.councilpost.domain.repository.StudentCouncilPostRepository;
import com.campus.campus.domain.place.domain.repository.PlaceRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PartnershipScheduler {

	private final StudentCouncilPostRepository studentCouncilPostRepository;
	private final PlaceRepository placeRepository;

	@Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
	@Transactional
	public void refreshPlacePartnershipStatus() {

		LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

		Set<Long> placeIds = new HashSet<>();

		// 제휴글이 존재하는 place
		placeIds.addAll(
			studentCouncilPostRepository.findDistinctPartnershipPlaceIds()
		);

		// 현재 isPartnership = true 인 place
		placeIds.addAll(
			placeRepository.findCurrentPartnershipPlaceIds()
		);

		for (Long placeId : placeIds) {
			placeRepository.findById(placeId).ifPresent(place -> {

				boolean hasActivePartnership =
					studentCouncilPostRepository.existsActivePartnershipByPlaceId(
						placeId,
						now
					);

				if (hasActivePartnership) {
					place.makePartnershipTrue();
				} else {
					place.makePartnershipFalse();
				}
			});
		}
	}
}
