package com.example.demo.controller;

import com.example.demo.exception.EntityNotFoundException;
import com.example.demo.model.Author;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.service.AuthorService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;
    
    @PostMapping
    public ResponseEntity<Author> addAuthor(@RequestBody Author author) {

        return ResponseEntity.ok(authorService.createAuthor(author));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Author> getAuthorById(@PathVariable Long id) {
        return authorService.getAuthor(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Author>> getAllAuthor() {
        return ResponseEntity.ok(authorService.getAllAuthor());
    }

    @PutMapping
    public ResponseEntity<Author> updateAuthor(@RequestBody Author author) {
        return authorService.updateAuthor(author)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void deleteAuthor(@PathVariable Long id) throws EntityNotFoundException {
        authorService.deleteAuthor(id);
    }
}
