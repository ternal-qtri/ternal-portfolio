package com.ternal.ternalportfolio.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "short_description", length = 500)
    private String shortDescription;

    @Column(name = "type", length = 50)
    private String type; // PERSONAL, TEAM

    @Column(name = "role", length = 100)
    private String role; // Ví dụ: "Lead Backend Developer"

    @Column(name = "timeframe", length = 50)
    private String timeframe; // Ví dụ: "01/2026 – 04/2026"

    @Column(name = "cover_image", length = 500)
    private String coverImage;

    @Column(name = "cover_image_public_id", length = 255)
    private String coverImagePublicId;

    @Column(name = "video_url", length = 500)
    private String videoUrl;

    @Column(name = "github_url", length = 500)
    private String githubUrl;

    @Column(name = "tags", length = 255)
    private String tags; // Ví dụ: "Spring Boot, PostgreSQL, Docker"

    @Column(name = "detailed_description", columnDefinition = "TEXT")
    private String detailedDescription;

    @Column(name = "features", columnDefinition = "TEXT")
    private String features;

    @Column(name = "lessons_learned", columnDefinition = "TEXT")
    private String lessonsLearned;

    @Column(name = "challenges", columnDefinition = "TEXT")
    private String challenges;

    @Column(name = "status", length = 50)
    private String status; // ACTIVE, COMPLETED

    @Builder.Default
    @Column(name = "order_index")
    private Integer orderIndex = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Transient
    public java.util.List<String> getTop3Tags() {
        if (tags == null || tags.isBlank()) {
            return java.util.Collections.emptyList();
        }
        return java.util.Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(t -> !t.isEmpty())
                .limit(3)
                .toList();
    }

    @Transient
    public java.util.List<String> getAllTags() {
        if (tags == null || tags.isBlank()) {
            return java.util.Collections.emptyList();
        }
        return java.util.Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(t -> !t.isEmpty())
                .toList();
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
