package integrationtest;

import com.example.demo.Demo17Application;
import com.example.demo.model.Author;
import com.example.demo.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.core.Is.is;

@SpringBootTest(classes = Demo17Application.class)
public class AuthorControllerIntegrationTest extends AbstractIntegrationTest{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthorRepository authorRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void clearData() { authorRepository.deleteAll();}

    @Test
    void testGetAuthor() throws Exception {
        Author author = Author.builder().name("Author Name").build();
        authorRepository.save(author);

        mockMvc.perform(get("/authors/" + author.getId()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Author actual = objectMapper.readValue(result.getResponse().getContentAsString(), Author.class);
                    assertEquals(author, actual);
                });
    }

    @Test
    void testGetAllAuthor() throws Exception {
        Author author = Author.builder().name("Author Name").build();
        Author author2 = Author.builder().build();
        authorRepository.save(author);
        authorRepository.save(author2);

        assertEquals(2, authorRepository.findAll().size());

        mockMvc.perform(get("/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)));
    }

    @Test
    void testPostAuthor() throws Exception {
        Author author = Author.builder().name("Author Name").build();

        mockMvc.perform(post("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(author)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Author actual = objectMapper.readValue(result.getResponse().getContentAsString(), Author.class);
                    assertEquals(author.getName(),actual.getName());
                });
    }

    @Test
    void testPutAuthor() throws Exception {
        Author author = Author.builder().name("Author Name").build();
        Author savedAuthor = authorRepository.save(author);
        Author newAuthor = Author.builder().name("new Name").id(savedAuthor.getId()).build();

        mockMvc.perform(put("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newAuthor)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Author actual = objectMapper.readValue(result.getResponse().getContentAsString(), Author.class);
                    assertEquals(newAuthor.getName(), actual.getName());
                });
    }

    @Test
    void testDeleteAuthor() throws Exception{
        Author author = Author.builder().name("name").build();
        Author savedAuthor = authorRepository.save(author);

        mockMvc.perform(delete("/authors/" + savedAuthor.getId()))
                .andExpect(status().isOk())
                .andExpect((result) -> assertEquals(Optional.empty(), authorRepository.findById(author.getId())));
    }
    
}
