package ru.yandex.practicum.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import junit.framework.TestCase;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.comment.CommentDto;
import ru.yandex.practicum.comment.CommentFullDto;
import ru.yandex.practicum.comment.CommentMapper2;
import ru.yandex.practicum.comment.service.CommentService;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CommentController.class)
@AutoConfigureMockMvc
public class CommentControllerTest extends TestCase {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CommentService commentService;

    @Autowired
    private MockMvc mvc;

    private final EasyRandom generator = new EasyRandom();

    @Test
    public void shouldSaveNewComment() throws Exception {
        mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        CommentFullDto commentFullDto = generator.nextObject(CommentFullDto.class);
        when(commentService.addComment(Mockito.anyInt(), Mockito.anyInt(),
                Mockito.any(CommentDto.class)))
                .thenReturn(commentFullDto);
        mvc.perform(post("/users/{userId}/comments")
                        .param("userId", "0")
                        .content(mapper.writeValueAsString(commentFullDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }
}