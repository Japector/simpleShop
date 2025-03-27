package org.japector.shopping.controller;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.japector.shopping.entity.UserEntity;
import org.japector.shopping.model.ItemDto;
import org.japector.shopping.model.LoginDto;
import org.japector.shopping.model.UserDto;
import org.japector.shopping.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = Controller.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class),
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;


    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
    }

    @Test
    public void testRegistrationShouldReturn200WhenCalledWithValidInput() throws Exception {
        //Given
        UserDto userDto = new UserDto("John", "Doe", "john.doe@example.com", "password123");
        UserEntity mockUserEntity = new UserEntity();

        when(userService.registerUser(any(String.class), any(String.class), any(String.class), any(String.class)))
                .thenReturn(mockUserEntity);
        //When-then
        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Successful registration!"));
    }

    @Test
    public void testRegistrationShouldReturn400WhenCalledWithInvalidInput() throws Exception {
        //Given
        UserDto userDto = new UserDto("", "", "notanemail", "");
        //When-then
        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegistrationShouldReturn401WhenRegistrationFailed() throws Exception {
        //Given
        UserDto userDto = new UserDto("John", "Doe", "invalid_email@exmaple.com", "password123");

        when(userService.registerUser(any(String.class), any(String.class), any(String.class), any(String.class)))
                .thenReturn(null);

        //When-then
        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateShouldReturnSuccessfulUpdateWhenUpdateWithValidUser() throws Exception {
        //Given
        UserDto userDto = new UserDto("John", "Doe", "john.doe@example.com", "password123");
        UserDto userDtoUpdated = new UserDto("John", "Doe", "john.doe@example.com", "newPassword123");

        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("user", userDto);
        when(userService.updateUser(any(UserDto.class))).thenReturn(userDtoUpdated);

        //When-then
        mockMvc.perform(put("/update")
                        .session(mockHttpSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Successful update!"));
    }

    @Test
    void testUpdateShouldReturnNotFoundWhenCalledWithNonExistingUser() throws Exception {
        //Given
        UserDto userDto = new UserDto("Non", "Existing", "non.existing@example.com", "password123");
        when(userService.updateUser(any(UserDto.class))).thenReturn(null);
        session.setAttribute("user", userDto);
        //When-then
        mockMvc.perform(put("/update")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Client cannot be found!"));
    }


    @Test
    void testSaveShoppingShouldSaveShoppingListSuccessfullyWhenALoggedInUserCallsIt() throws Exception {
        //Given
        UserDto userDto = new UserDto("John", "Doe", "john@example.com", "password");
        session.setAttribute("user", userDto);

        List<ItemDto> itemDtoList = Arrays.asList(
                new ItemDto(1L,"Apples",3.0, 2.99, "kg"),
                new ItemDto(2L,"Oranges",3.0, 1.99, "kg")
        );

        //When-then
        mockMvc.perform(post("/saveShoppingList")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDtoList)))
                .andExpect(status().isOk())
                .andExpect(content().string("Successful update!"));
    }

    @Test
    void testSaveShoppingShouldNotSaveShoppingListSuccessfullyWhenAUserSessionIsInvalid() throws Exception {
        //Given
        UserDto userDto = new UserDto("John", "Doe", null, "password");
        session.setAttribute("user", userDto);
        List<ItemDto> itemDtoList = Arrays.asList(
                new ItemDto(1L,"Apples",3.0, 2.99, "kg"),
                new ItemDto(2L,"Oranges",3.0, 1.99, "kg")
        );


        //When-then
        mockMvc.perform(post("/saveShoppingList")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDtoList)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Cannot update shopping list!"));
    }


    @Test
    void testFetchShoppingListShouldReturnsItemListWhenAnAuthenticatedUserCalls() throws Exception {
        //Given
        UserDto userDto = new UserDto("John", "Doe", "john@example.com", "password");
        session.setAttribute("user", userDto);

        List<ItemDto> expectedList = Arrays.asList(
                new ItemDto(1L,"Apples",3.0, 2.99, "kg"),
                new ItemDto(2L,"Oranges",3.0, 1.99, "kg"));
        when(userService.findAllItemsForUser(any(UserDto.class))).thenReturn(expectedList);

        //When-then
        mockMvc.perform(get("/fetchShoppingList").session(session))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedList)));
    }

    @Test
    void testFetchShoppingListShouldReturnsUnauthorizedWhenANonAuthenticatedUserCalls() throws Exception {
        //Given
        session.setAttribute("user", new UserDto());
        //When-then
        mockMvc.perform(get("/fetchShoppingList").session(session))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Cannot get shopping list!"));
    }

    @Test
    void testDeleteUserShouldReturnsItemListWhenAnAuthenticatedUserCalls() throws Exception {
        //Given
        UserDto userDto = new UserDto("John", "Doe", "john@example.com", "password");
        UserEntity userEntity = new UserEntity(); // Populate as necessary
        userEntity.setId(1L);
        session.setAttribute("user", userDto);

        when(userService.findUserByEmail(userDto.getEmail())).thenReturn(userEntity);
        doNothing().when(userService).deleteUserById(userEntity.getId());

        // When-then
        mockMvc.perform(delete("/delete").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted"));
    }

    @Test
    void testDeleteUserShouldReturnsUnauthorizedWhenANonAuthenticatedUserCalls() throws Exception {
        //Given
        session.setAttribute("user", new UserDto());
        //When-then
        mockMvc.perform(delete("/delete").session(session))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("User not found in session!"));
    }


    @Test
    void testGetUsersShouldReturnsListOfUsersWhenCalled() throws Exception {
        //Given
        session.setAttribute("user", new UserDto());
        List<UserDto> expectedUsers = Arrays.asList(
                new UserDto("John", "Doe", "john@example.com", "password123"),
                new UserDto("Jane", "Doe", "jane@example.com", "securePassword")
        );

        when(userService.findAllUsers()).thenReturn(expectedUsers);

        //When-then
        MvcResult result = mockMvc.perform(get("/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                        .andReturn();
        String responseContent = result.getResponse().getContentAsString();

        System.out.println("Expected Users JSON: " + objectMapper.writeValueAsString(expectedUsers));
        System.out.println("Actual Response Content: " + responseContent);

    }


    @Test
    void testGetAUserShouldReturnsUserDtoWhenValidSessionIsCalling() throws Exception {
        //Given
        UserDto userDto = new UserDto("John", "Doe", "john@example.com", "password123");
        session.setAttribute("user", userDto);

        //When-then
        mockMvc.perform(get("/getUser").session(session))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));

    }

    @Test
    void testLoginShouldReturnsSuccessMessageWhenCredentialsAreValid() throws Exception {
        //Given
        LoginDto loginDto = new LoginDto("john@example.com", "password123");
        UserDto userDto = new UserDto("John", "Doe", "john@example.com", "password123");

        when(userService.authenticateUser(any(LoginDto.class))).thenReturn(userDto);

        //When-then
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("User authenticated successfully"));
    }

    @Test
    void testLoginShouldReturnsUnauthorizedMessageWhenCredentialsAreInvalid() throws Exception {
        //Given
        LoginDto loginDto = new LoginDto("john@example.com", "wrongpassword");
        when(userService.authenticateUser(any(LoginDto.class))).thenReturn(null);

        //When-then
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid email or password"));
    }


    @Test
    void testLogoutShouldInvalidatesSessionAndReturnsSuccessMessageWhenCalled() throws Exception {
        //Given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", new UserDto()); // Assuming there's a user logged in

        //When-then
        mockMvc.perform(get("/logout").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Logged out successfully")));

    }

}