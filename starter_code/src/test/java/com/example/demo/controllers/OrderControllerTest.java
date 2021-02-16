package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;
    private final UserRepository userRepository = mock(UserRepository.class);
    private final OrderRepository orderRepository = mock(OrderRepository.class);

    private final Item testItem = new Item();
    private final Item testItem2 = new Item();
    private final List<Item> itemList = new ArrayList<Item>();
    private final List<UserOrder> orderList = new ArrayList<UserOrder>();
    private final User testUser = new User();
    private final UserOrder testOrder = new UserOrder();

    @Before
    public void setup() {
        orderController = new OrderController();
        ReflectionTestUtils.setField(orderController, "userRepository", userRepository);
        ReflectionTestUtils.setField(orderController, "orderRepository", orderRepository);

        testItem.setId(1L);
        testItem.setName("square widget");
        testItem.setPrice(new BigDecimal("12.99"));
        testItem.setDescription("a square widget for doing stuff");

        testItem2.setId(2L);
        testItem2.setName("triangle widget");
        testItem2.setPrice(new BigDecimal("15.99"));
        testItem2.setDescription("a triangular widget for doing stuff");

        itemList.add(testItem);
        itemList.add(testItem2);

        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setPassword("password");
        testUser.setCart(new Cart());
        testUser.getCart().addItem(testItem);

        testOrder.setId(1L);
        testOrder.setItems(itemList);
        testOrder.setUser(testUser);
        testOrder.setTotal(BigDecimal.valueOf(12.99 + 15.99));
        orderList.add(testOrder);

        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(testUser);
        when(orderRepository.findByUser(testUser)).thenReturn(orderList);
    }

    @Test
    public void submit() {
        ResponseEntity<UserOrder> response = orderController.submit(testUser.getUsername());

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());

        UserOrder returnedOrder = response.getBody();
        Assert.assertNotNull(returnedOrder);

        List<Item> orderItems = returnedOrder.getItems();
        Assert.assertEquals(1, orderItems.size());
        Assert.assertEquals(testItem, orderItems.get(0));
    }

    @Test
    public void submitUserNotFound() {
        ResponseEntity<UserOrder> response = orderController.submit("joe schmoe");
        Assert.assertEquals(404, response.getStatusCodeValue());
        Assert.assertNull(response.getBody());
    }

    @Test
    public void getOrdersForUser() {
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(testUser.getUsername());

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());

        List<UserOrder> returnedOrders = response.getBody();
        Assert.assertNotNull(returnedOrders);
        Assert.assertEquals(1, returnedOrders.size());

        UserOrder returnedOrder = returnedOrders.get(0);
        Assert.assertEquals(testOrder, returnedOrder);
    }

    @Test
    public void getOrdersForUserUserNotFound() {
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("joe schmoe");
        Assert.assertEquals(404, response.getStatusCodeValue());
        Assert.assertNull(response.getBody());
    }
}
