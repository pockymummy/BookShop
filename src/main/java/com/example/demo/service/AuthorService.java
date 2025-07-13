package com.example.demo.service;

import com.example.demo.exception.EntityNotFoundException;
import com.example.demo.model.Author;
import com.example.demo.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    public Optional<Author> getAuthor(Long id) {
        return authorRepository.findById(id);
    }

    public List<Author> getAllAuthor() {
        return authorRepository.findAll();
    }

    public Author createAuthor(Author author) {
        return authorRepository.save(author);
    }

    public Optional<Author> updateAuthor(Author author) {
        return authorRepository.findById(author.getId())
                .map((Author a) -> authorRepository.save(author));
    }

    public void deleteAuthor(Long id) throws EntityNotFoundException {
        Author a = authorRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        authorRepository.delete(a);
    }
}