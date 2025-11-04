package org.example.Barnes;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BarnesTest {

    static class StubBookDb implements BookDatabase {
        @Override
        public Book findByISBN(String ISBN){
            if ("111".equals(ISBN)) return new Book ("111", 10, 100);
            return null;
        }
    }

    static class StubBuyProcess implements BuyBookProcess {
        int lastAmount = -1;
        Book lastBook = null;
        @Override
        public void buyBook (Book book,  int amount){
            lastBook = book;
            lastAmount = amount;
        }
    }

    @Test
    @DisplayName("specification-based")
    /*for when a book exists and the price is quantity times unit price*/
    void priceIsQuantityTimesUnitPrice(){
        var db = new StubBookDb();
        var proc = new StubBuyProcess();
        var store = new BarnesAndNoble(db, proc);
        Map<String,Integer> order = new HashMap<>();
        order.put("111", 3);

        PurchaseSummary result = store.getPriceForCart(order);
        assertNotNull(result);
        assertEquals(30, result.getTotalPrice());
        assertTrue(result.getUnavailable().isEmpty());
        assertEquals(3, proc.lastAmount);
        assertNotNull(proc.lastBook);
        assertEquals(new Book("111", 10, 0), proc.lastBook);
    }

    @Test
    @DisplayName("structural-based")
    /*For when marks unavailable when book is missing while returning null for null cart */
    void returnsNullForNullCart(){
        var db = new StubBookDb(){
            @Override public Book findByISBN(String ISBN) { return null; }

        };
        var proc = new StubBuyProcess();
        var store = new BarnesAndNoble(db, proc);
        assertNull(store.getPriceForCart(null));
        Map<String, Integer> order = new HashMap<>();
        order.put("999", 2);
        PurchaseSummary result = store.getPriceForCart(order);
        assertEquals(0, result.getTotalPrice());
        assertFalse(result.getUnavailable().isEmpty());
    }
}
