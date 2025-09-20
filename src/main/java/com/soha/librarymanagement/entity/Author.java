package com.soha.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "authors")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Lob
    private String bio;
}
