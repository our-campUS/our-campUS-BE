package com.campus.campus.domain.studentcouncilpost.domain.entity;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @Column(nullable = false)
    private String imageUrl;
    private int sequence;


    @Enumerated(EnumType.STRING)
    private ImageStatus status;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private StudentCouncilPost post;

    @Builder
    public PostImage(StudentCouncilPost post, String imageUrl, int sequence) {
        this.post = post;
        this.imageUrl = imageUrl;
        this.sequence = sequence;
    }
}
