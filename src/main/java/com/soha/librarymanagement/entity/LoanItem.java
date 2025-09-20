package com.soha.librarymanagement.entity;

import com.soha.librarymanagement.entity.BookCopy;
import com.soha.librarymanagement.entity.Loan;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "loan_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LoanItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "copy_id", nullable = false)
    private BookCopy copy;

    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at", nullable = false)
    private LocalDateTime updateAt;

    @Column(name = "create_by", nullable = false)
    private Integer createBy;

    @Column(name = "fine_amount")
    private Double fineAmount = 0.0;

    @PrePersist
    protected void onCreate() {
        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateAt = LocalDateTime.now();
    }
}
