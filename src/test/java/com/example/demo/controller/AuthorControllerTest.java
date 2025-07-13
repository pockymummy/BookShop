package com.example.demo.controller;

import com.example.demo.exception.EntityNotFoundException;
import com.example.demo.model.Author;
import com.example.demo.model.Publisher;
import com.example.demo.service.AuthorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorController.class)
public class AuthorControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AuthorService authorService;

    private Author defaultAuthor;
    private Publisher defaultPublisher;
    private List<Author> defaultAuthorList;

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @BeforeEach
    void setup() {
        defaultPublisher = Publisher.builder()
                .id(1L)
                .name("game")
                .address("address")
                .build();
        defaultAuthor = Author.builder()
                .id(1L)
                .name("jame")
                .biography("biography")
                .publisher(defaultPublisher)
                .build();
        defaultAuthorList = new ArrayList<>();
        defaultAuthorList.add(defaultAuthor);
        defaultAuthorList.add(defaultAuthor);
    }

    @Test
    void addAuthorShouldReturnAuthor() throws Exception {
        when(authorService.createAuthor(defaultAuthor)).thenReturn(defaultAuthor);

        mockMvc.perform(post("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(defaultAuthor)))
                .andExpect(status().isOk())
                .andExpect((result) -> {
                    Author actual = objectMapper.readValue(result.getResponse().getContentAsString(), Author.class);
                    assertEquals(actual, defaultAuthor);
                });
    }

    @Test
    void getExistedAuthorShouldReturnAuthor() throws Exception {
        when(authorService.getAuthor(defaultAuthor.getId())).thenReturn(Optional.of(defaultAuthor));

        mockMvc.perform(get("/authors/" + defaultAuthor.getId()))
                .andExpect(status().isOk())
                .andExpect((result -> {
                    Author acutual = objectMapper.readValue(result.getResponse().getContentAsString(), Author.class);
                    assertEquals(acutual, defaultAuthor);
                }));
    }

    @Test
    void getNonExistedAuthorShouldReturnNotFound() throws Exception {
        when(authorService.getAuthor(any())).thenReturn(Optional.empty());

        mockMvc.perform(get("/authors/" + defaultAuthor.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllAuthorShouldReturnAuthorList() throws Exception {
        when(authorService.getAllAuthor()).thenReturn(defaultAuthorList);

        mockMvc.perform(get("/authors"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    assertEquals(objectMapper.writeValueAsString(defaultAuthorList),result.getResponse().getContentAsString());
                });
    }

    @Test
    void updateExistingAuthorShouldReturnUpdatedAuthor() throws Exception {
        when(authorService.updateAuthor(defaultAuthor)).thenReturn(Optional.of(defaultAuthor));

        mockMvc.perform(put("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(defaultAuthor)))
                .andExpect(status().isOk())
                .andExpect((result -> {
                    Author acutal = objectMapper.readValue(result.getResponse().getContentAsString(), Author.class);
                    assertEquals(defaultAuthor, acutal);
                }));
    }

    @Test
    void updateNonExistAuthorShouldReturnNotFound() throws Exception {
        when(authorService.updateAuthor(any())).thenReturn(Optional.empty());

        mockMvc.perform(put("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(defaultAuthor)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteExistingAuthorShouldNotThrow() throws Exception {
        mockMvc.perform(delete("/authors/1"))
                .andExpect(status().isOk());
        verify(authorService,times(1)).deleteAuthor(1L);
    }

    @Test
    void deleteNonExistedAuthorShouldReturnNotFound() throws Exception {
        doThrow(new EntityNotFoundException())
                .when(authorService)
                .deleteAuthor(1L);
        mockMvc.perform(delete("/authors/1"))
                .andExpect(status().isNotFound());
    }
}
