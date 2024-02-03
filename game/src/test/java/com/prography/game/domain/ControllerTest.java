package com.prography.game.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prography.game.domain.room.controller.RoomController;
import com.prography.game.domain.room.service.RoomService;
import com.prography.game.domain.user.controller.UserController;
import com.prography.game.domain.user.service.UserInitService;
import com.prography.game.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        UserController.class,
        RoomController.class
})
public abstract class ControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected UserService userService;
    @MockBean
    protected UserInitService userInitService;
    @MockBean
    protected RoomService roomService;
}
