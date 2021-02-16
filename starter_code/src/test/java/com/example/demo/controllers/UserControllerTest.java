package com.example.demo.controllers;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    private final CreateUserRequest testCreateUserRequest = new CreateUserRequest();
    private final User testUser = new User();

    @Before
    public void setup() {
        userController = new UserController();
        ReflectionTestUtils.setField(userController, "userRepository", userRepository);
        ReflectionTestUtils.setField(userController, "cartRepository", cartRepository);
        ReflectionTestUtils.setField(userController, "bCryptPasswordEncoder", encoder);

        testUser.setId(1);
        testUser.setPassword("password");
        testUser.setUsername("username");

        testCreateUserRequest.setUsername("username");
        testCreateUserRequest.setPassword("password");
        testCreateUserRequest.setConfirmPassword("password");
    }

    @Test
    public void findById() throws Exception {
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(testUser));
        final ResponseEntity<User> response = userController.findById(1L);

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());

        User returnedUser = response.getBody();

        Assert.assertNotNull(returnedUser);
        Assert.assertEquals(testUser.getId(), returnedUser.getId());
        Assert.assertEquals(testUser.getUsername(), returnedUser.getUsername());
        Assert.assertEquals(testUser.getPassword(), returnedUser.getPassword());
    }

    @Test
    public void findByUsername() throws Exception {
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(testUser);
        final ResponseEntity<User> response = userController.findByUserName(testUser.getUsername());

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());

        User returnedUser = response.getBody();

        Assert.assertNotNull(returnedUser);
        Assert.assertEquals(testUser.getId(), returnedUser.getId());
        Assert.assertEquals(testUser.getUsername(), returnedUser.getUsername());
        Assert.assertEquals(testUser.getPassword(), returnedUser.getPassword());
    }

    @Test
    public void createUser() throws Exception {
        when(encoder.encode("testPassword")).thenReturn(testCreateUserRequest.getPassword());
        final ResponseEntity<User> response = userController.createUser(testCreateUserRequest);

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());

        User returnedUser = response.getBody();
        Assert.assertNotNull(returnedUser);
        Assert.assertEquals(0, returnedUser.getId());
        Assert.assertEquals(testCreateUserRequest.getUsername(), returnedUser.getUsername());
    }
}
