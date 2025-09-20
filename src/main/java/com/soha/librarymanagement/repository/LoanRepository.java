package com.soha.librarymanagement.repository;

import com.soha.librarymanagement.entity.Loan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Integer>, JpaSpecificationExecutor<Loan> {

    @Query("""
        select l
        from Loan l
        left join fetch l.member m
        left join fetch l.createdBy u
        where l.id = :id
    """)
    Optional<Loan> findOneShallow(@Param("id") Integer id);

    @Query("""
        select l
        from Loan l
        left join fetch l.member m
        left join fetch l.createdBy u
        where l.id in :ids
    """)
    List<Loan> findBatchByIdWithSingles(@Param("ids") Collection<Integer> ids);

}