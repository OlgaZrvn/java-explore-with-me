package ru.yandex.practicum.event.controller;

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
import ru.yandex.practicum.event.EventFullDto;
import ru.yandex.practicum.event.NewEventDto;
import ru.yandex.practicum.event.service.EventService;
import ru.yandex.practicum.user.User;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PrivateEventController.class)
@AutoConfigureMockMvc
public class PrivateEventControllerTest extends TestCase {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private EventService eventService;

    @Autowired
    private MockMvc mvc;

    private final EasyRandom generator = new EasyRandom();

    @Test
    public void shouldSaveNewEvent() throws Exception {
        mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        EventFullDto eventFullDto = generator.nextObject(EventFullDto.class);
        when(eventService.addNewEvent(Mockito.anyInt(),
                Mockito.any(NewEventDto.class)))
                .thenReturn(eventFullDto);
        User user = generator.nextObject(User.class);
        mvc.perform(post("/users/{userId}/events")
                        .content(mapper.writeValueAsString(user.getId()))
                        .content(mapper.writeValueAsString(eventFullDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }
}