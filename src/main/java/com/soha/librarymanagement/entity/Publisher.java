package com.soha.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "publisher")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Publisher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "founder_year")
    private Integer founderYear;


}
