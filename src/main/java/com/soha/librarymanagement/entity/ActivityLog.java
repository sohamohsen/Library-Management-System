package com.soha.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_log")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();

    @Column(nullable = false, length = 60)
    private String action;

    @Column(nullable = false, length = 64)
    private String entity;

    @Column(name = "entity_id")
    private Integer entityId;

    @Lob
    private String details;

    @Column(length = 64)
    private String ip;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "create_by", nullable = false)
    private SystemUser createdBy;
}
