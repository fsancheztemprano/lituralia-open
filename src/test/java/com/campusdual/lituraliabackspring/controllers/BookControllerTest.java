package com.campusdual.lituraliabackspring.controllers;

import static com.campusdual.lituraliabackspring.controllers.AbstractRestControllerTest.asJsonString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.campusdual.lituraliabackspring.api.model.BookDTO;
import com.campusdual.lituraliabackspring.services.BookService;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class BookControllerTest {

    public static final String REST_URL = "/books";
    public static final String HAMLET = "Hamlet";
    public static final String HAMLET_ISBN = "123456";
    @Mock
    BookService service;

    @InjectMocks
    BookController controller;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                                 .setControllerAdvice(new RestResponseEntityExceptionHandler())
                                 .build();
    }

    @Test
    void getAllBooks() throws Exception {
        //given
        BookDTO book1 = BookDTO.builder()
                               .bookId(1L)
                               .isbn("123456")
                               .title(HAMLET)
                               .build();
        BookDTO book2 = BookDTO.builder()
                               .bookId(2L)
                               .isbn("123457")
                               .title("MacBeth")
                               .build();

        when(service.getAllBooks()).thenReturn(Arrays.asList(book1, book2));

        //when
        mockMvc.perform(get(REST_URL)
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.books", hasSize(2)));
    }

    @Test
    void getEmployeeById() throws Exception {
        //given
        BookDTO book1 = BookDTO.builder()
                               .bookId(1L)
                               .isbn(HAMLET_ISBN)
                               .title(HAMLET)
                               .build();

        when(service.getBookById(anyLong())).thenReturn(book1);

        //when
        mockMvc.perform(get(REST_URL + "/1")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.title", equalTo(HAMLET)))
               .andExpect(jsonPath("$.isbn", equalTo(HAMLET_ISBN)));
    }

    @Test
    void createBook() throws Exception {
        //given
        BookDTO book1 = BookDTO.builder()
                               .bookId(1L)
                               .isbn(HAMLET_ISBN)
                               .title(HAMLET)
                               .build();

        BookDTO returnDTO = BookDTO.builder()
                                   .bookId(1L)
                                   .isbn(HAMLET_ISBN)
                                   .title(HAMLET)
                                   .build();

        when(service.createBook(any())).thenReturn(returnDTO);

        //when/then
        mockMvc.perform(post(REST_URL)
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(book1)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.title", equalTo(HAMLET)))
               .andExpect(jsonPath("$.isbn", equalTo(HAMLET_ISBN)));
    }

    @Test
    void updateBook() throws Exception {
        //given
        BookDTO book1 = BookDTO.builder()
                               .bookId(1L)
                               .isbn(HAMLET_ISBN)
                               .title(HAMLET)
                               .build();

        BookDTO returnDTO = BookDTO.builder()
                                   .bookId(1L)
                                   .isbn(HAMLET_ISBN)
                                   .title(HAMLET)
                                   .build();

        when(service.updateBook(anyLong(), any(BookDTO.class))).thenReturn(returnDTO);

        //when/then
        mockMvc.perform(put(REST_URL + "/1")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(book1)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.title", equalTo(HAMLET)))
               .andExpect(jsonPath("$.isbn", equalTo(HAMLET_ISBN)));
    }

    @Test
    void deleteBook() throws Exception {
        mockMvc.perform(delete(REST_URL + "/1")
                            .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk());

        verify(service).deleteBookById(anyLong());
    }

    @Test
    public void testNotFoundException() throws Exception {

        when(service.getBookById(anyLong())).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get(REST_URL + "/222")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound());
    }
}