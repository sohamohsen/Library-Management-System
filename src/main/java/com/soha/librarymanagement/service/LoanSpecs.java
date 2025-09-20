package com.soha.librarymanagement.service;

import com.soha.librarymanagement.dto.LoanSearchCriteria;
import com.soha.librarymanagement.entity.Loan;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class LoanSpecs {

    public static Specification<Loan> byCriteria(LoanSearchCriteria c) {
        return (root, query, cb) -> {
            // allow count queries to be distinct-friendly
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                // join singles; don’t fetch collections here
                root.join("member", JoinType.LEFT);
                root.join("createdBy", JoinType.LEFT);
            }

            List<Predicate> ps = new ArrayList<>();

            if (c.getMemberId() != null) {
                ps.add(cb.equal(root.get("member").get("id"), c.getMemberId()));
            }
            if (c.getStatus() != null) {
                ps.add(cb.equal(root.get("status"), c.getStatus()));
            }
            if (c.getCreatedById() != null) {
                ps.add(cb.equal(root.get("createdBy").get("id"), c.getCreatedById()));
            }
            if (c.getCreatedFrom() != null) {
                ps.add(cb.greaterThanOrEqualTo(root.get("createAt"), c.getCreatedFrom()));
            }
            if (c.getCreatedTo() != null) {
                ps.add(cb.lessThanOrEqualTo(root.get("createAt"), c.getCreatedTo()));
            }
            if (c.getDueFrom() != null) {
                ps.add(cb.greaterThanOrEqualTo(root.get("dueAt"), c.getDueFrom()));
            }
            if (c.getDueTo() != null) {
                ps.add(cb.lessThanOrEqualTo(root.get("dueAt"), c.getDueTo()));
            }
            if (Boolean.TRUE.equals(c.getOverdueOnly())) {
                var now = LocalDateTime.now();
                ps.add(cb.lessThan(root.get("dueAt"), now));
                ps.add(root.get("status").in(0, 3));
            }
            if (c.getQ() != null && !c.getQ().isBlank()) {
                var like = "%" + c.getQ().trim().toLowerCase() + "%";
                var memberJoin = root.join("member", JoinType.LEFT);
                var emailLike = cb.like(cb.lower(memberJoin.get("email")), like);
                var nameLike  = cb.like(cb.lower(memberJoin.get("fullName")), like);
                ps.add(cb.or(emailLike, nameLike));
            }

            return ps.isEmpty() ? cb.conjunction() : cb.and(ps.toArray(new Predicate[0]));
        };
    }

    private LoanSpecs() {}
}
