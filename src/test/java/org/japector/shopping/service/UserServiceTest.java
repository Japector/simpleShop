package org.japector.shopping.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.japector.shopping.entity.ItemEntity;
import org.japector.shopping.entity.UserEntity;
import org.japector.shopping.exception.EmailAlreadyInUseException;
import org.japector.shopping.exception.UnknownUserException;
import org.japector.shopping.model.ItemDto;
import org.japector.shopping.model.LoginDto;
import org.japector.shopping.model.UserDto;
import org.japector.shopping.repository.ItemRepository;
import org.japector.shopping.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private final UserEntity trueUserEntity = new UserEntity();
    private final UserEntity falseUserEntity = new UserEntity();
    private final UserDto trueUserDto = new UserDto();
    private final UserDto falseUserDto = new UserDto();
    private final LoginDto trueLoginDto = new LoginDto();
    private final LoginDto falseLoginDto  = new LoginDto();


    private final ItemEntity trueItemEntity = new ItemEntity();
    private final ItemEntity falseItemEntity = new ItemEntity();

    private final ItemDto trueItemDto = new ItemDto();
    private final ItemDto falseItemDto = new ItemDto();
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {

        trueUserEntity.setFirstName("Test");
        trueUserEntity.setLastName("Elek");
        trueUserEntity.setEmail("test.elek@test.com");
        trueUserEntity.setPassword("password");

        falseUserEntity.setFirstName("Non");
        falseUserEntity.setLastName("User");
        falseUserEntity.setEmail("non.user@test.com");
        falseUserEntity.setPassword("password");

        trueUserDto.setFirstName(trueUserEntity.getFirstName());
        trueUserDto.setLastName(trueUserEntity.getLastName());
        trueUserDto.setEmail(trueUserEntity.getEmail());
        trueUserDto.setPassword(trueUserEntity.getPassword());

        falseUserDto.setFirstName(falseUserEntity.getFirstName());
        falseUserDto.setLastName(falseUserEntity.getLastName());
        falseUserDto.setEmail(falseUserEntity.getEmail());
        falseUserDto.setPassword(falseUserEntity.getPassword());


        trueLoginDto.setEmail(trueUserEntity.getEmail());
        trueLoginDto.setPassword(trueUserEntity.getPassword());

        falseLoginDto.setEmail(falseUserEntity.getEmail());
        falseLoginDto.setPassword(falseUserEntity.getPassword());




        trueItemEntity.setPrice(1.0);
        trueItemEntity.setQuantity(1.0);

        falseItemEntity.setPrice(10.0);
        falseItemEntity.setQuantity(10.0);

        trueItemDto.setName("Test");
        trueItemDto.setPrice(trueItemEntity.getPrice());
        trueItemDto.setQuantity(trueItemEntity.getQuantity());

        falseItemDto.setName("Nothing");
        falseItemDto.setPrice(falseItemEntity.getPrice());
        falseItemDto.setQuantity(falseItemEntity.getQuantity());

    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void testRegisterUserShouldThrowsEmailAlreadyInUseExceptionWhenEmailAlreadyExists() {
        //Given
        String email = "test.elek@test.com";
        when(userRepository.findByEmail(email)).thenReturn(new UserEntity());
        //When-Then
        assertThrows(EmailAlreadyInUseException.class, () -> {
            userService.registerUser("First", "Last", email, "password123");
        });
    }

    @Test
    void testRegisterUserShouldRegisterNewUsersWhenCalled() {
        //Given
        String email = "test.elek@test.com";
        String firstName = "Test";
        String lastName = "Elek";
        String rawPassword = "password";
        String hashedPassword = "hashedPassword";

        when(userRepository.findByEmail(email)).thenReturn(null);
        when(passwordEncoder.encode(rawPassword)).thenReturn(hashedPassword);
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //When
        UserEntity savedUser = userService.registerUser(firstName, lastName, email, rawPassword);

        //Then
        assertNotNull(savedUser);
        assertEquals(hashedPassword, savedUser.getPassword(), "The password should be hashed.");
        verify(passwordEncoder).encode(rawPassword);
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void findUserByEmail() {
    }


    @Test
    void testFindUserByIdShouldReturnUserDtoWhenUserExists() {
        //Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(trueUserEntity));

        //When
        UserDto result = userService.findUserById(userId);

        //Then
        assertNotNull(result);
        assertEquals(trueUserEntity.getFirstName(), result.getFirstName());
        assertEquals(trueUserEntity.getLastName(), result.getLastName());
        assertEquals(trueUserEntity.getEmail(), result.getEmail());
    }

    @Test
    void testFindUserByIdShouldThrowExceptionWhenUserDoesNotExist() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UnknownUserException.class, () -> {
            userService.findUserById(userId);
        });
    }


    @Test
    void testFindAllUsersShouldReturnAllUsersWhenCalled() {
        //Given
        UserEntity user1 = new UserEntity();
        user1.setEmail("user1@example.com");
        user1.setFirstName("User");
        user1.setLastName("One");

        UserEntity user2 = new UserEntity();
        user2.setEmail("user2@example.com");
        user2.setFirstName("User");
        user2.setLastName("Two");

        List<UserEntity> mockUsers = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(mockUsers);

        //When
        List<UserDto> resultDto = userService.findAllUsers();

        //Then
        assertNotNull(resultDto);
        assertEquals(mockUsers.size(), resultDto.size(), "The size of the returned list should match the " +
                "number of UserEntity objects returned by findAll.");
        List<String> userEmails = resultDto.stream()
                .map(UserDto::getEmail)
                .toList();
        assertTrue(userEmails.containsAll(Arrays.asList("user1@example.com", "user2@example.com")), "The " +
                "list should contain all user emails.");
    }

    @Test
    void findUserById() {
    }



    @Test
    void testUpdateUserShouldUpdateUserWhenCalled() {
        //Given
        when(userRepository.findByEmail(anyString())).thenReturn(trueUserEntity);
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setEmail("test.elek@test.com");
        updatedUserDto.setFirstName("NewFirstName");
        updatedUserDto.setLastName("NewLastName");
        updatedUserDto.setPassword("newPassword");
        //When
        UserDto resultDto = userService.updateUser(updatedUserDto);

        //Then
        assertEquals(updatedUserDto.getFirstName(), resultDto.getFirstName());
        assertEquals(updatedUserDto.getLastName(), resultDto.getLastName());
        assertEquals("encodedPassword", trueUserEntity.getPassword());
        verify(passwordEncoder).encode("newPassword");
        verify(userRepository).save(trueUserEntity);
    }


    @Test
    void testUpdateUserShouldNotUpdatePasswordWhenEmpty() {
        //Given
        when(userRepository.findByEmail(anyString())).thenReturn(trueUserEntity);
        UserDto userDtoWithEmptyPassword = new UserDto();
        userDtoWithEmptyPassword.setEmail("existing@example.com");
        userDtoWithEmptyPassword.setPassword("");
        when(userRepository.save(any(UserEntity.class))).thenReturn(trueUserEntity);

        //When
        UserDto resultDto = userService.updateUser(userDtoWithEmptyPassword);

        //Then
        assertEquals("password", trueUserEntity.getPassword(), "Password should not change");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository).save(trueUserEntity);
    }

    @Test
    void testDeleteUserByIdShouldDeleteWhenCalledWithExistingUser() {
        //Given
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        //When
        userService.deleteUserById(userId);
        //Then
        verify(userRepository).deleteById(userId);
    }

    @Test
    void testDeleteUserByIdShouldThrowExceptionWhenCalledWithNonExistingUser() {
        //Given
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);
        //When-Then
        assertThrows(UnknownUserException.class, () -> userService.deleteUserById(userId));
    }

    @Test
    void testAuthenticateUserShouldThrowUserNotFoundWhenThereIsNoSuchUser() {
        //Given-When
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        //Then
        assertThrows(UnknownUserException.class, () -> {
            userService.authenticateUser(falseLoginDto);
        });
    }

    @Test
    void testAuthenticateUserShouldReturnNullWhenThePasswordIsIncorrect() {
        //Given
        when(userRepository.findByEmail(anyString())).thenReturn(trueUserEntity);
       //When
        UserDto result = userService.authenticateUser(falseLoginDto);
        //Then
        assertNull(result);
    }

    @Test
    void testAuthenticateUserShouldAuthenticateWhenCalledByAValidUser() {
        //Given
        when(userRepository.findByEmail(anyString())).thenReturn(trueUserEntity);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        //When
        UserDto result = userService.authenticateUser(trueLoginDto);
        //Then
        assertEquals(trueUserDto.getEmail(),result.getEmail());
    }



    @Test
    void testConvertToDtoShouldConvertAUserEntityToUserDtoWhenCalled(){
        //Given-When
        UserDto resultDto = userService.convertToDto(trueUserEntity);
        //Then
        assertEquals(trueUserEntity.getFirstName(), resultDto.getFirstName(), "It should match");
        assertEquals(trueUserEntity.getLastName(), resultDto.getLastName(), "It should match");
        assertEquals(trueUserEntity.getEmail(), resultDto.getEmail(), "It should match");
    }



}