package com.example.demo.controller;

import com.example.demo.exception.PathVariableAndRequestBodyInconsistentException;
import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private static final Logger log = LoggerFactory.getLogger(BookController.class);
    private final BookRepository bookRepository;

    @PostMapping
    @CacheEvict(value = "books", allEntries = true) //invalidate the entire book cache
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        Book savedBook =  bookRepository.save(book);
        return ResponseEntity.ok(savedBook);
    }

    @GetMapping("/{id}")
    @Cacheable("books")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Optional<Book> book = bookRepository.findById(id);
        return book.map(ResponseEntity::ok).orElseGet(() ->
                ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBook () {
        return ResponseEntity.ok(bookRepository.findAll());
    }

    @PutMapping("/{id}")
    @CacheEvict(value = "books", allEntries = true)
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book book) throws PathVariableAndRequestBodyInconsistentException {
        if (book.getId() != null && !book.getId().equals(id)) {
            log.warn("Id in body {} and path {} are inconsistent", book.getId(), id);
            throw new PathVariableAndRequestBodyInconsistentException();
        }else {
            return bookRepository.findById(id)
                    .map((Book foundBook) -> bookRepository.save(book))
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }
    }
}
