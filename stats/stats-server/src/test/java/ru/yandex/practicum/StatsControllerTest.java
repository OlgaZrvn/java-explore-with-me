package ru.yandex.practicum;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StatsController.class)
@AutoConfigureMockMvc
class StatsControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private StatsService statsService;

    @Autowired
    private MockMvc mvc;

    private final EasyRandom generator = new EasyRandom();

    @Test
    public void shouldSaveNewHit() throws Exception {
        mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        EndpointHit endpointHit = generator.nextObject(EndpointHit.class);
        when(statsService.addHit(Mockito.any(EndpointHit.class)))
                .thenReturn(endpointHit);
        mvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(endpointHit))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldNotSaveHitWithBlankApp() throws Exception {
        mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        EndpointHit endpointHit = generator.nextObject(EndpointHit.class);
        endpointHit.setApp("");
        when(statsService.addHit(Mockito.any(EndpointHit.class)))
                .thenReturn(endpointHit);
        mvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(endpointHit))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotSaveHitWithBlankUri() throws Exception {
        mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        EndpointHit endpointHit = generator.nextObject(EndpointHit.class);
        endpointHit.setUri("");
        when(statsService.addHit(Mockito.any(EndpointHit.class)))
                .thenReturn(endpointHit);
        mvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(endpointHit))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotSaveHitWithBlankIp() throws Exception {
        mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        EndpointHit endpointHit = generator.nextObject(EndpointHit.class);
        endpointHit.setIp("");
        when(statsService.addHit(Mockito.any(EndpointHit.class)))
                .thenReturn(endpointHit);
        mvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(endpointHit))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldGetViewStats() throws Exception {
        mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        List<ViewStats> viewStatsList = List.of(
                generator.nextObject(ViewStats.class),
                generator.nextObject(ViewStats.class));

        when(statsService.getViewStatsListWithoutUris(Mockito.any(LocalDateTime.class),
                Mockito.any(LocalDateTime.class),
                Mockito.anyBoolean()))
                .thenReturn(viewStatsList);

        mvc.perform(get("/stats")
                        .param("start", "2023-05-04 00:00:00")
                        .param("end", "2023-06-04 00:00:00")
                        .param("unique", "true"))
                .andExpect(status().isOk());
    }
}