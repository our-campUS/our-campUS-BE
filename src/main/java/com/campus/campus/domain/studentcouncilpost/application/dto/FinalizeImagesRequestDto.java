package com.campus.campus.domain.studentcouncilpost.application.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FinalizeImagesRequestDto {

    private String thumbnailTempUrl;   // 필요 시
    private List<String> images;       // tempUrl list
}
