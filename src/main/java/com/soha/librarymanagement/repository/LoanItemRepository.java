package com.soha.librarymanagement.repository;

import com.soha.librarymanagement.entity.LoanItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface LoanItemRepository extends JpaRepository<LoanItem, Integer> {

    @Query("""
        select li
        from LoanItem li
        join fetch li.copy c
        join fetch c.book b
        where li.loan.id = :loanId
        order by li.id
    """)
    List<LoanItem> findDetailedByLoanId(@Param("loanId") Integer loanId);

    @Query("""
        select li
        from LoanItem li
        join fetch li.copy c
        join fetch c.book b
        where li.loan.id = :loanId and li.id in :itemIds
        order by li.id
    """)
    List<LoanItem> findDetailedByLoanIdAndItemIds(@Param("loanId") Integer loanId,
                                                  @Param("itemIds") Collection<Integer> itemIds);

    @Query("""
        select li
        from LoanItem li
        join fetch li.copy c
        join fetch c.status s
        join fetch c.book b
        where li.loan.id = :loanId
        order by li.id
    """)
    List<LoanItem> findDetailedWithStatusByLoanId(@Param("loanId") Integer loanId);

    List<LoanItem> findByLoan_Id(Integer id);

    boolean existsByLoan_IdAndCopy_Id(Integer loanId, Integer id);

    @Query("""
        select li
        from LoanItem li
        join fetch li.copy c
        join fetch c.status s
        join fetch c.book b
        where li.loan.id in :loanIds
        order by li.id
    """)
    List<LoanItem> findDetailedByLoanIds(@Param("loanIds") Collection<Integer> loanIds);
}
