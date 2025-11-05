package org.example.Amazon;

import org.example.Amazon.Cost.DeliveryPrice;
import org.example.Amazon.Cost.ItemType;
import org.example.Amazon.Cost.RegularCost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AmazonIntegrationTest {
    static class InMemoryCart implements ShoppingCart {
        private final List<Item> items = new ArrayList<>();
        @Override
        public void add(Item item) {
            items.add(item);
        }
        @Override
        public List<Item> getItems(){
            return Collections.unmodifiableList(items);
        }
        @Override
        public int numberOfItems(){
            return items.size();
        }
        public void clear(){
            items.clear();
        }
    }

    private InMemoryCart cart;
    @BeforeEach
    void resetCart() {
        cart = new InMemoryCart();
        cart.clear();
    }

    @Test
    @DisplayName("specification-based")
    void regularCostPlusDelivery_isAppliedOverAllItems(){
        var rules = List.of(new RegularCost(), new DeliveryPrice());
        var amazon = new Amazon(cart, rules);
        cart.add(new Item(ItemType.OTHER, "Book", 2, 10.0));
        cart.add(new Item(ItemType.ELECTRONIC, "Mouse", 1, 15.0));
        double total = amazon.calculate();

        assertTrue(total >= 35.0);
    }

    @Test
    @DisplayName("structural-based")
    void emptyCartCostsZero(){
        /* even with rules */
        var rules = List.of(new RegularCost(), new DeliveryPrice());
        var amazon = new Amazon(cart, rules);
        assertEquals(0.0, amazon.calculate(), 1e-9);
    }
}
