package com.soha.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "books",
        indexes = { @Index(name = "uk_books_isbn", columnList = "isbn", unique = true) })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 20)
    private String isbn;

    @Column(nullable = false, length = 240)
    private String title;

    @Column(length = 40)
    private String language;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "publication_year")
    private Integer publicationYear;

    @Column(length = 40)
    private String edition;

    @Lob
    private String summary;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();

    @Column(name = "update_at", nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();

    @Column(name = "create_by", nullable = false)
    private Integer createBy;

}
