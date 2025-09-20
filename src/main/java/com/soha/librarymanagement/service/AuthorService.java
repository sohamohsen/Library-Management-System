package com.soha.librarymanagement.service;

import com.soha.librarymanagement.dto.AuthorDto;
import com.soha.librarymanagement.dto.CreateAuthorDto;
import com.soha.librarymanagement.entity.Author;
import com.soha.librarymanagement.repository.AuthorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    @Transactional
    public CreateAuthorDto createAuthor(CreateAuthorDto dto) {
        String fullName = dto.fullName().trim();
        log.info("Create Author request: {}", fullName);

        if (authorRepository.existsByFullNameIgnoreCase(fullName)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Author already exists");
        }

        Author author = Author.builder()
                .fullName(fullName)
                .bio(dto.bio() == null ? null : dto.bio().trim())
                .build();

        try {
            Author saved = authorRepository.save(author);
            return toCreateDto(saved);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Author already exists");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create author");
        }
    }

    @Transactional
    public CreateAuthorDto updateAuthor(Integer id, AuthorDto dto) {
        String newName = dto.fullName().trim();
        String newBio = dto.bio() == null ? null : dto.bio().trim();

        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"));

        if (authorRepository.existsByFullNameIgnoreCaseAndIdNot(newName, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Another author with the same name exists");
        }

        author.setFullName(newName);
        author.setBio(newBio);

        try {
            Author saved = authorRepository.save(author);
            return toCreateDto(saved);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Author name conflict");
        }
    }

    @Transactional
    public void deleteAuthor(Integer id) {
        if (!authorRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found");
        }
        authorRepository.deleteById(id);
    }

    @Transactional
    public AuthorDto getAuthorById(Integer id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"));
        return toDto(author);
    }

    @Transactional
    public List<AuthorDto> getAllAuthors() {
        return authorRepository.findAll().stream().map(this::toDto).toList();
    }

    private AuthorDto toDto(Author a) {
        return new AuthorDto(a.getId(), a.getFullName(), a.getBio());
    }

    private CreateAuthorDto toCreateDto(Author a) {
        return new CreateAuthorDto(a.getId(), a.getFullName(), a.getBio());
    }
}