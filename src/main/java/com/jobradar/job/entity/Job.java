package com.jobradar.job.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id")
    private com.jobradar.auth.entity.User employer;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String company;

    private String salary;

    @Enumerated(EnumType.STRING)
    private JobType type;

    private String skillsRequired;

    @Column(nullable = false)
    private Double lat;

    @Column(nullable = false)
    private Double lng;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    @Builder.Default
    private boolean isActive = true;

    @Builder.Default
    private boolean isDeleted = false;

    @Builder.Default
    private Long viewCount = 0L;

    @Builder.Default
    private Long applicationCount = 0L;

    @Builder.Default
    private boolean isUrgent = false;

    @Builder.Default
    private boolean isBoosted = false;

    @Builder.Default
    private boolean isFastHiring = false;

    private LocalDateTime fastHiringAt;

    @Builder.Default
    private boolean isApproved = false;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    public enum JobType {
        PART_TIME, INTERNSHIP, GIG
    }

    public enum JobStatus {
        ACTIVE, CLOSED
    }
}
