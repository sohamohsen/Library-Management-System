package com.soha.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "book_copies")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BookCopy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false, length = 64)
    private String barcode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "status_id", nullable = false)
    private BookCopyStatus status;

    @Column(name = "acquired_at", nullable = false)
    private LocalDateTime acquiredAt = LocalDateTime.now();

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();

    @Column(name = "update_at", nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();

    @Column(name = "create_by", nullable = false)
    private Integer createBy;
}
