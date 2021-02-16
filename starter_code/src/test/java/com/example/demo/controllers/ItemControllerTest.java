package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;
    private final ItemRepository itemRepository = mock(ItemRepository.class);

    private final Item testItem1 = new Item();
    private final Item testItem2 = new Item();
    private final List<Item> testItemList = new ArrayList<Item>();

    @Before
    public void setup() {
        itemController = new ItemController();
        ReflectionTestUtils.setField(itemController, "itemRepository", itemRepository);

        testItem1.setId(1L);
        testItem1.setName("round widget");
        testItem1.setPrice(new BigDecimal("2.99"));
        testItem1.setDescription("a round widget thingy.  it's alright.");

        testItem2.setId(2L);
        testItem2.setName("square widget");
        testItem2.setPrice(new BigDecimal("12.99"));
        testItem2.setDescription("a square widget thingy.  much nicer.");

        testItemList.add(testItem1);
        testItemList.add(testItem2);
    }

    @Test
    public void getItems() {
        when(itemRepository.findAll()).thenReturn(testItemList);
        final ResponseEntity<List<Item>> response = itemController.getItems();

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());

        List<Item> returnedItems = response.getBody();
        Assert.assertNotNull(returnedItems);
        Assert.assertEquals(returnedItems.get(0), testItem1);
        Assert.assertEquals(returnedItems.get(1), testItem2);
    }

    @Test
    public void getItemById() {
        when(itemRepository.findById(testItem1.getId())).thenReturn(java.util.Optional.of(testItem1));
        final ResponseEntity<Item> response = itemController.getItemById(1L);

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());

        Item returnedItem = response.getBody();
        Assert.assertNotNull(returnedItem);
        Assert.assertEquals(returnedItem, testItem1);
    }

    @Test
    public void getItemsByName() {
        when(itemRepository.findByName("testItems")).thenReturn(testItemList);
        final ResponseEntity<List<Item>> response = itemController.getItemsByName("testItems");

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());

        List<Item> returnedItems = response.getBody();
        Assert.assertNotNull(returnedItems);
        Assert.assertEquals(returnedItems.get(0), testItem1);
        Assert.assertEquals(returnedItems.get(1), testItem2);
    }
}
