package com.soha.librarymanagement.service;// src/main/java/com/soha/librarymanagement/spec/MemberSpecifications.java

import com.soha.librarymanagement.entity.Members;
import org.springframework.data.jpa.domain.Specification;

public final class MemberSpecifications {

    private MemberSpecifications() {} // utility class

    public static Specification<Members> textContains(String q) {
        return (root, query, cb) -> {
            if (q == null || q.trim().isEmpty()) return cb.conjunction();
            String like = "%" + q.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("fullName")), like),
                    cb.like(cb.lower(root.get("email")), like),
                    cb.like(cb.lower(root.get("phone")), like)
            );
        };
    }

    public static Specification<Members> activeEq(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) return cb.conjunction();
            return cb.equal(root.get("active"), active);
        };
    }
}
