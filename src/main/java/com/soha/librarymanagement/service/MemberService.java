package com.soha.librarymanagement.service;

import com.soha.librarymanagement.dto.CreateMemberDto;
import com.soha.librarymanagement.dto.MemberDto;
import com.soha.librarymanagement.dto.MemberResponse;
import com.soha.librarymanagement.dto.MemberSearchCriteria;
import com.soha.librarymanagement.entity.Members;
import com.soha.librarymanagement.entity.SystemUser;
import com.soha.librarymanagement.repository.MembersRepository;
import com.soha.librarymanagement.repository.SystemUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MembersRepository repo;
    private final SystemUserRepository userRepo;

    @Transactional
    public MemberResponse create(CreateMemberDto dto, String username) {
        log.info("Create member: email={}, phone={}", dto.getEmail(), dto.getPhone());

        if (repo.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        if (dto.getPhone() != null && !dto.getPhone().isBlank() && repo.existsByPhone(dto.getPhone())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone already exists");
        }

        // 1) هات المستخدم الحالي من الـ SecurityContext (لازم الـ JwtFilter يحط principal فيه)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == "anonymousUser") {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authentication");
        }
        Integer creatorId = userRepo.findByUsername(username)
                .map(SystemUser::getId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));


        Members m = Members.builder()
                .code(generateCode())
                .fullName(dto.getFullName().trim())
                .email(dto.getEmail().trim().toLowerCase())
                .phone(dto.getPhone() == null ? null : dto.getPhone().trim())
                .active(true)
                .createBy(creatorId)
                .joinedOn(dto.getJoinedOn() == null ? LocalDate.now() : dto.getJoinedOn())
                .build();

        try {
            Members saved = repo.save(m);
            return toResponse(saved);
        } catch (DataIntegrityViolationException e) {
            e.getMostSpecificCause();
            String msg = e.getMostSpecificCause().getMessage();
            log.error("Create member failed: {}", msg, e);
            if (msg != null && msg.toLowerCase().contains("create_by")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "create_by is required");
            }
            if (msg != null && (msg.toLowerCase().contains("email") || msg.toLowerCase().contains("phone"))) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Duplicate email or phone");
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid member data");
        }
    }
    public MemberResponse getById(Integer id) {
        Members m = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));
        return toResponse(m);
    }

    public Page<MemberResponse> search(MemberSearchCriteria criteria, Pageable pageable) {
        Specification<Members> spec = Specification.allOf(
                MemberSpecifications.textContains(criteria.getQ()),
                MemberSpecifications.activeEq(criteria.getActive())
        );

        Page<Members> page = repo.findAll(spec, pageable);
        return page.map(this::toResponse);
    }

    @Transactional
    public MemberResponse update(Integer id, MemberDto dto) {
        Members existing = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));

        if (repo.existsByEmailIgnoreCaseAndIdNot(dto.getEmail(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        if (dto.getPhone() != null && !dto.getPhone().isBlank()
                && repo.existsByPhoneAndIdNot(dto.getPhone(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone already exists");
        }

        existing.setFullName(dto.getFullName().trim());
        existing.setEmail(dto.getEmail().trim().toLowerCase());
        existing.setPhone(dto.getPhone() == null ? null : dto.getPhone().trim());
        existing.setActive(Boolean.TRUE.equals(dto.getActive()));
        existing.setUpdateAt(LocalDateTime.now());

        Members saved = repo.save(existing);
        return toResponse(saved);
    }

    @Transactional
    public void deactivate(Integer id) {
        Members m = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));
        m.setActive(false);
        m.setUpdateAt(LocalDateTime.now());
        repo.save(m);
    }

    private MemberResponse toResponse(Members m) {
        return MemberResponse.builder()
                .id(m.getId())
                .fullName(m.getFullName())
                .email(m.getEmail())
                .phone(m.getPhone())
                .active(m.isActive())
                .build();
    }

    private String generateCode() {
        return "MBR-" + System.currentTimeMillis();
    }
}