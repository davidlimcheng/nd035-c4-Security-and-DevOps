package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    private CartController cartController;

    private final CartRepository cartRepository = mock(CartRepository.class);
    private final ItemRepository itemRepository = mock(ItemRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);

    private final ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
    private final Item testItem = new Item();
    private final Item testItem2 = new Item();
    private final User testUser = new User();

    @Before
    public void setup() {
        cartController = new CartController();
        ReflectionTestUtils.setField(cartController, "cartRepository", cartRepository);
        ReflectionTestUtils.setField(cartController, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(cartController, "userRepository", userRepository);

        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setPassword("password");
        testUser.setCart(new Cart());

        testItem.setId(1L);
        testItem.setName("square widget");
        testItem.setPrice(new BigDecimal("12.99"));
        testItem.setDescription("a square widget for doing stuff");

        testItem2.setId(2L);
        testItem2.setName("round widget");
        testItem2.setPrice(new BigDecimal("2.99"));
        testItem2.setDescription("a round widget for doing stuff");

        testUser.getCart().addItem(testItem);

        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(testUser);
        when(itemRepository.findById(testItem.getId())).thenReturn(java.util.Optional.of(testItem));
        when(itemRepository.findById(testItem2.getId())).thenReturn(java.util.Optional.of(testItem2));
    }

    @Test
    public void addToCart() {
        modifyCartRequest.setItemId(2L);
        modifyCartRequest.setQuantity(2);
        modifyCartRequest.setUsername("testUser");

        ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());

        Cart returnedCart = response.getBody();
        Assert.assertNotNull(returnedCart);

        List<Item> items = returnedCart.getItems();
        Assert.assertNotNull(items);
        Assert.assertEquals(3, items.size());
        Assert.assertEquals(testItem, items.get(0));
        Assert.assertEquals(testItem2, items.get(1));
        Assert.assertEquals(testItem2, items.get(2));
    }

    @Test
    public void addToCartUserNotFound() {
        modifyCartRequest.setUsername("");
        ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);
        Assert.assertEquals(404, response.getStatusCodeValue());
        Assert.assertNull(response.getBody());
    }

    @Test
    public void addToCartItemNotFound() {
        modifyCartRequest.setUsername("testUser");
        modifyCartRequest.setItemId(4L);

        ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);
        Assert.assertEquals(404, response.getStatusCodeValue());
        Assert.assertNull(response.getBody());
    }

    @Test
    public void removeFromCart() {
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(1);
        modifyCartRequest.setUsername("testUser");

        ResponseEntity<Cart> response = cartController.removeFromCart(modifyCartRequest);

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());

        Cart returnedCart = response.getBody();
        Assert.assertNotNull(returnedCart);

        List<Item> items = returnedCart.getItems();
        Assert.assertNotNull(items);
        Assert.assertEquals(0, items.size());
    }

}
