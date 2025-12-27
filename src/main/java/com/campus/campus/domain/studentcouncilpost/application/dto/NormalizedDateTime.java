package com.campus.campus.domain.studentcouncilpost.application.dto;

import java.time.LocalDateTime;

public record NormalizedDateTime(
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
) {}
