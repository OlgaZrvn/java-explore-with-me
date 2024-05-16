package ru.yandex.practicum.user.controller;

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
import ru.yandex.practicum.user.NewUserRequest;
import ru.yandex.practicum.user.User;
import ru.yandex.practicum.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest extends TestCase {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    private final EasyRandom generator = new EasyRandom();

    @Test
    public void shouldSaveNewUser() throws Exception {
        mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        User user = new User(0, "Name", "email@mail.com");
        NewUserRequest newUserRequest = new NewUserRequest("Name", "email@mail.com");
        when(userService.addNewUser(Mockito.any(NewUserRequest.class)))
                .thenReturn(user);
        mvc.perform(post("/admin/users")
                        .content(mapper.writeValueAsString(newUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldGetUsers() throws Exception {
        mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        List<User> users = List.of(
                generator.nextObject(User.class),
                generator.nextObject(User.class));

        when(userService.getUsersByIds(Mockito.any(List.class),
                Mockito.anyInt(),
                Mockito.anyInt()))
                .thenReturn(users);

        mvc.perform(get("/admin/users")
                        .param("ids", "1, 2")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }
}