package com.soha.librarymanagement.service;

import com.soha.librarymanagement.dto.*;
import com.soha.librarymanagement.entity.*;
import com.soha.librarymanagement.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class LoanService {

    public static final int LOAN_STATUS_OPEN = 0;
    public static final int LOAN_STATUS_RETURNED = 1;
    public static final int LOAN_STATUS_CANCELLED = 2;
    public static final int LOAN_STATUS_OVERDUE = 3;

    private final LoanRepository loanRepo;
    private final LoanItemRepository itemRepo;
    private final BookCopyRepository copyRepo;
    private final MembersRepository membersRepo;
    private final SystemUserRepository userRepo;
    private final BookCopyStatusRepository statusRepo;

    private Map<String, BookCopyStatus> loadStatuses() {
        Map<String, BookCopyStatus> map = new HashMap<>();
        map.put("AVAILABLE", statusRepo.findByStatusIgnoreCase("AVAILABLE")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "BookCopyStatus AVAILABLE not found")));
        map.put("LOANED", statusRepo.findByStatusIgnoreCase("LOANED")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "BookCopyStatus LOANED not found")));
        map.put("DAMAGED", statusRepo.findByStatusIgnoreCase("DAMAGED")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "BookCopyStatus DAMAGED not found")));
        map.put("LOST", statusRepo.findByStatusIgnoreCase("LOST")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "BookCopyStatus LOST not found")));
        return map;
    }

    private LoanResponse toResponse(Loan loan) {
        List<LoanItem> items = itemRepo.findByLoan_Id(loan.getId());

        List<LoanItemResponse> itemDtos = items.stream().map(li -> {
            BookCopy copy = li.getCopy();
            Book book = copy.getBook();

            return LoanItemResponse.builder()
                    .id(li.getId())
                    .copyId(copy.getId())
                    .barcode(copy.getBarcode())
                    .bookId(book.getId())
                    .bookTitle(book.getTitle())
                    .bookIsbn(book.getIsbn())
                    .returnedAt(li.getReturnedAt())
                    .fineAmount(li.getFineAmount())
                    .build();
        }).toList();

        return LoanResponse.builder()
                .id(loan.getId())
                .memberId(loan.getMember().getId())
                .status(loan.getStatus())
                .dueAt(loan.getDueAt())
                .createAt(loan.getCreateAt())
                .updateAt(loan.getUpdateAt())
                .createdById(loan.getCreatedBy().getId())
                .items(itemDtos)
                .build();
    }


    @Transactional
    public LoanResponse create(CreateLoanRequest req, String username) {
        Members member = membersRepo.findById(req.getMemberId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));

        if (req.getBarcodes() == null || req.getBarcodes().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No barcodes provided");

        List<String> inputBarcodes = req.getBarcodes().stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();

        if (inputBarcodes.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No valid barcodes provided");

        List<BookCopy> copies = copyRepo.findAllByBarcodeIn(inputBarcodes);

        Set<String> found = copies.stream().map(BookCopy::getBarcode).collect(java.util.stream.Collectors.toSet());
        List<String> missing = inputBarcodes.stream().filter(b -> !found.contains(b)).toList();
        if (!missing.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Barcodes not found: " + String.join(", ", missing));
        }

        var statuses = loadStatuses();

        for (BookCopy c : copies) {
            BookCopyStatus s = c.getStatus();
            if (s.equals(statuses.get("LOANED")) || s.equals(statuses.get("DAMAGED")) || s.equals(statuses.get("LOST"))) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Copy with barcode " + c.getBarcode() + " is not available");
            }
        }

        LocalDateTime dueAt = Optional.ofNullable(req.getDueAt())
                .orElse(LocalDateTime.now().plusDays(14));

        SystemUser creator = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));

        Loan loan = Loan.builder()
                .member(member)
                .status(LOAN_STATUS_OPEN)
                .dueAt(dueAt)
                .createdBy(creator)   // ManyToOne
                .build();
        loan = loanRepo.save(loan);

        for (BookCopy c : copies) {
            LoanItem li = LoanItem.builder()
                    .loan(loan)
                    .copy(c)
                    .createBy(creator.getId())
                    .fineAmount(0.0)
                    .build();
            itemRepo.save(li);

            c.setStatus(statuses.get("LOANED"));
            c.setUpdateAt(LocalDateTime.now());
            copyRepo.save(c);
        }

        if (LocalDateTime.now().isAfter(dueAt)) {
            loan.setStatus(LOAN_STATUS_OVERDUE);
            loanRepo.save(loan);
        }

        return toResponse(loan);
    }

    @Transactional
    public LoanResponse addItems(Integer loanId, AddLoanItemsRequest req) {
        Loan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan not found"));
        if (loan.getStatus() != LOAN_STATUS_OPEN && loan.getStatus() != LOAN_STATUS_OVERDUE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loan is not open");
        }

        var statuses = loadStatuses();

        List<BookCopy> copies = copyRepo.findAllById(req.getCopyIds());
        if (copies.size() != req.getCopyIds().size())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Some copyIds not found");

        for (BookCopy c : copies) {
            if (!c.getStatus().equals(statuses.get("AVAILABLE")))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Copy " + c.getId() + " is not available");
            if (itemRepo.existsByLoan_IdAndCopy_Id(loanId, c.getId()))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Copy " + c.getId() + " already in loan");

            LoanItem li = LoanItem.builder()
                    .loan(loan)
                    .copy(c)
                    .createBy(loan.getCreatedBy().getId())
                    .fineAmount(0.0)
                    .build();
            itemRepo.save(li);

            c.setStatus(statuses.get("LOANED"));
            c.setUpdateAt(LocalDateTime.now());
            copyRepo.save(c);
        }
        return toResponse(loan);
    }

    @Transactional
    public LoanResponse returnItems(Integer loanId, LoanItemReturnRequest req) {
        Loan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan not found"));

        LocalDateTime returnedAt = Optional.ofNullable(req.getReturnedAt()).orElse(LocalDateTime.now());

        List<LoanItem> items = itemRepo.findAllById(req.getItemIds());
        if (items.size() != req.getItemIds().size())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Some itemIds not found");

        var statuses = loadStatuses();

        for (LoanItem li : items) {
            if (!li.getLoan().getId().equals(loanId))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item not in this loan");

            if (li.getReturnedAt() != null) continue;

            li.setReturnedAt(returnedAt);
            long daysLate = ChronoUnit.DAYS.between(loan.getDueAt().toLocalDate(), returnedAt.toLocalDate());
            li.setFineAmount(daysLate > 0 ? daysLate * 5.0 : 0.0);
            itemRepo.save(li);

            BookCopy copy = li.getCopy();
            copy.setStatus(statuses.get("AVAILABLE"));
            copy.setUpdateAt(LocalDateTime.now());
            copyRepo.save(copy);
        }

        boolean allReturned = itemRepo.findByLoan_Id(loanId).stream().allMatch(i -> i.getReturnedAt() != null);
        if (allReturned) {
            loan.setStatus(LOAN_STATUS_RETURNED);
        } else if (LocalDateTime.now().isAfter(loan.getDueAt())) {
            loan.setStatus(LOAN_STATUS_OVERDUE);
        }
        loanRepo.save(loan);
        return toResponse(loan);
    }

    @Transactional
    public Page<LoanResponse> search(LoanSearchCriteria criteria, Pageable pageable) {
        // page over loans (no collection fetch here)
        Page<Loan> page = loanRepo.findAll(LoanSpecs.byCriteria(criteria), pageable);

        if (page.isEmpty()) {
            return Page.empty(pageable);
        }

        // Batch-load singles (member, createdBy) if you didn’t already fetch them in the spec:
        List<Integer> ids = page.getContent().stream().map(Loan::getId).toList();
        List<Loan> enrichedLoans = loanRepo.findBatchByIdWithSingles(ids);
        Map<Integer, Loan> loanById = new HashMap<>();
        for (Loan l : enrichedLoans) loanById.put(l.getId(), l);

        // Batch-load items + copy + status + book
        List<LoanItem> allItems = itemRepo.findDetailedByLoanIds(ids);
        Map<Integer, List<LoanItem>> itemsByLoan = new HashMap<>();
        for (LoanItem li : allItems) {
            itemsByLoan.computeIfAbsent(li.getLoan().getId(), k -> new ArrayList<>()).add(li);
        }

        // Map to DTO
        List<LoanResponse> content = page.getContent().stream().map(l -> {
            Loan loan = loanById.getOrDefault(l.getId(), l);
            List<LoanItem> items = itemsByLoan.getOrDefault(l.getId(), List.of());

            List<LoanItemResponse> itemDtos = items.stream().map(li -> {
                var copy = li.getCopy();
                var book = copy.getBook();
                return LoanItemResponse.builder()
                        .id(li.getId())
                        .copyId(copy.getId())
                        .barcode(copy.getBarcode())
                        .bookId(book.getId())
                        .bookTitle(book.getTitle())
                        .bookIsbn(book.getIsbn())
                        .returnedAt(li.getReturnedAt())
                        .fineAmount(li.getFineAmount())
                        .build();
            }).toList();

            return LoanResponse.builder()
                    .id(loan.getId())
                    .memberId(loan.getMember().getId())
                    .status(loan.getStatus())
                    .dueAt(loan.getDueAt())
                    .createAt(loan.getCreateAt())
                    .updateAt(loan.getUpdateAt())
                    .createdById(loan.getCreatedBy().getId())
                    .items(itemDtos)
                    .build();
        }).toList();

        return new org.springframework.data.domain.PageImpl<>(content, pageable, page.getTotalElements());
    }

    @Transactional
    public LoanResponse close(Integer loanId) {
        Loan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan not found"));

        boolean allReturned = itemRepo.findByLoan_Id(loanId).stream().allMatch(i -> i.getReturnedAt() != null);
        if (!allReturned) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Items not fully returned");

        loan.setStatus(LOAN_STATUS_RETURNED);
        loanRepo.save(loan);
        return toResponse(loan);
    }

    @Transactional
    public LoanResponse cancel(Integer loanId) {
        Loan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan not found"));

        if (loan.getStatus() == LOAN_STATUS_RETURNED)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loan already returned");

        var statuses = loadStatuses();

        List<LoanItem> items = itemRepo.findByLoan_Id(loanId);
        for (LoanItem li : items) {
            if (li.getReturnedAt() == null) {
                BookCopy c = li.getCopy();
                c.setStatus(statuses.get("AVAILABLE"));
                c.setUpdateAt(LocalDateTime.now());
                copyRepo.save(c);
            }
        }
        loan.setStatus(LOAN_STATUS_CANCELLED);
        loanRepo.save(loan);
        return toResponse(loan);
    }

    public LoanResponse get(Integer id) {
        Loan loan = loanRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan not found"));
        return toResponse(loan);
    }
}