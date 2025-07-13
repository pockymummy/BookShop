package com.example.demo.service;

import com.example.demo.exception.EntityNotFoundException;
import com.example.demo.model.Author;
import com.example.demo.model.Publisher;
import com.example.demo.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorService authorService;

    private Author savedAuthor;
    private Publisher defaultPublisher;
    private List<Author> savedAuthorList;

    @BeforeEach
    void setup() {
        defaultPublisher = Publisher.builder().name("PacktPublishing").build();

        savedAuthor = Author.builder()
                .id(1L)
                .name("Author Name")
                .publisher(defaultPublisher)
                .biography("Biography of Author")
                .build();

        ArrayList<Author> list = new ArrayList<>();
        list.add(savedAuthor);
        list.add(savedAuthor);
        savedAuthorList = list;
    }

    @Test
    void givenExistingAuthorId_whenGetAuthor_thenReturnAuthor() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(savedAuthor));
        Optional<Author> author = authorService.getAuthor(1L);

        assertTrue(author.isPresent(), "Author should be found");
        assertEquals(1L, author.get().getId(), "Author ID should match");
    }

    @Test
    void givenNonExistingAuthorId_whenGetAuthor_thenReturnEmpty() {
        when(authorRepository.findById(2L)).thenReturn(Optional.empty());
        Optional<Author> author = authorService.getAuthor(2L);

        assertFalse(author.isPresent(), "Author should be empty");
    }

    @Test
    void givenAuthor_whenGetAllAuthor_thenReturnAuthorList() {
        when(authorRepository.findAll()).thenReturn(savedAuthorList);

        List<Author> listAuthor = authorService.getAllAuthor();

        assertEquals(2, listAuthor.size());
        assertAll(
                () -> assertEquals(savedAuthor.getId(),listAuthor.get(0).getId()),
                () -> assertEquals(savedAuthor.getBiography(),listAuthor.get(0).getBiography()),
                () -> assertEquals(savedAuthor.getName(),listAuthor.get(0).getName()),
                () -> assertEquals(savedAuthor.getPublisher(),listAuthor.get(0).getPublisher()),
                () -> assertEquals(savedAuthor.getId(),listAuthor.get(1).getId()),
                () -> assertEquals(savedAuthor.getBiography(),listAuthor.get(1).getBiography()),
                () -> assertEquals(savedAuthor.getName(),listAuthor.get(1).getName()),
                () -> assertEquals(savedAuthor.getPublisher(),listAuthor.get(1).getPublisher())
        );
    }

    @Test
    void givenAuthor_whenAddAuthor_thenReturnCreatedAuthor() {
        when(authorRepository.save(savedAuthor)).thenReturn(savedAuthor);

        Author author = authorService.createAuthor(savedAuthor);

        assertAll(
                () -> assertNotNull(author),
                () -> assertEquals(author.getId(), savedAuthor.getId()),
                () -> assertEquals(author.getName(), savedAuthor.getName()),
                () -> assertEquals(author.getBiography(), savedAuthor.getBiography()),
                () -> assertEquals(author.getPublisher(), savedAuthor.getPublisher())
        );
    }

    @Test
    void givenExistedAuthor_whenUpdateAuthor_thenReturnAuthor() {
        when(authorRepository.findById(savedAuthor.getId())).thenReturn(Optional.of(savedAuthor));
        when(authorRepository.save(savedAuthor)).thenReturn(savedAuthor);

        Optional<Author> author = authorService.updateAuthor(savedAuthor);

        assertAll(
                () -> assertTrue(author.isPresent()),
                () -> assertEquals(author.get().getId(), savedAuthor.getId()),
                () -> assertEquals(author.get().getName(), savedAuthor.getName()),
                () -> assertEquals(author.get().getPublisher(), savedAuthor.getPublisher()),
                () -> assertEquals(author.get().getBiography(), savedAuthor.getBiography())
        );
    }



    @Test
    void givenNonExistedAuthor_whenUpdateAuthor_thenReturnEmpty() {
        when(authorRepository.findById(savedAuthor.getId())).thenReturn(Optional.empty()) ;

        Optional<Author> author = authorService.updateAuthor(savedAuthor);

        assertFalse(author.isPresent(), "Author should be empty");
    }

    @Test
    void givenAuthorExist_whenDeleteAuthor_thenNoReturn() {
        when(authorRepository.findById(savedAuthor.getId())).thenReturn(Optional.of(savedAuthor));

        assertDoesNotThrow(() -> authorService.deleteAuthor(savedAuthor.getId()));
        verify(authorRepository).delete(savedAuthor);
    }

    @Test
    void givenAuthorNotExist_whenDeleteAuthor_thenThrow() {
        when(authorRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,() -> authorService.deleteAuthor(1L));
    }

}