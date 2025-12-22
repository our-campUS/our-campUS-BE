package com.campus.campus.domain.studentcouncilpost.domain.entity;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tempUrl;
    private String finalUrl;

    private int sequence;


    @Enumerated(EnumType.STRING)
    private ImageStatus status;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private StudentCouncilPost post;

    public void markFinal(String finalUrl) {
        this.finalUrl = finalUrl;
        this.status = ImageStatus.FINAL;
    }

}
