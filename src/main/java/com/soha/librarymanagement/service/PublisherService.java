package com.soha.librarymanagement.service;

import com.soha.librarymanagement.dto.CreatePublisherDto;
import com.soha.librarymanagement.dto.PublisherDto;
import com.soha.librarymanagement.entity.Publisher;
import com.soha.librarymanagement.repository.PublisherRepository;
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
public class PublisherService {

    private final PublisherRepository publisherRepository;

    @Transactional
    public CreatePublisherDto createPublisher(CreatePublisherDto dto) {
        String name = dto.name().trim();

        if (publisherRepository.existsByNameIgnoreCase(name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Publisher already exists");
        }

        Publisher publisher = Publisher.builder()
                .name(name)
                .founderYear(dto.founderYear())
                .build();

        try {
            Publisher saved = publisherRepository.save(publisher);
            return toCreateDto(saved);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Publisher already exists");
        }
    }

    @Transactional
    public CreatePublisherDto updatePublisher(Integer id, PublisherDto dto) {
        String newName = dto.name().trim();

        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Publisher not found"));

        if (publisherRepository.existsByNameIgnoreCaseAndIdNot(newName, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Another publisher with the same name exists");
        }

        publisher.setName(newName);
        publisher.setFounderYear(dto.founderYear());

        Publisher saved = publisherRepository.save(publisher);
        return toCreateDto(saved);
    }

    @Transactional
    public void deletePublisher(Integer id) {
        if (!publisherRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Publisher not found");
        }
        publisherRepository.deleteById(id);
    }

    @Transactional
    public PublisherDto getPublisherById(Integer id) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Publisher not found"));
        return toDto(publisher);
    }

    @Transactional
    public List<PublisherDto> getAllPublishers() {
        return publisherRepository.findAll().stream().map(this::toDto).toList();
    }

    private PublisherDto toDto(Publisher p) {
        return new PublisherDto(p.getId(), p.getName(), p.getFounderYear());
    }

    private CreatePublisherDto toCreateDto(Publisher p) {
        return new CreatePublisherDto(p.getId(), p.getName(), p.getFounderYear());
    }
}
