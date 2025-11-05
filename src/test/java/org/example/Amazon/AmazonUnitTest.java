package org.example.Amazon;

import org.example.Amazon.Cost.ItemType;
import org.example.Amazon.Cost.PriceRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AmazonUnitTest {
    static class CartStub implements ShoppingCart {
        private final List<Item> items = new ArrayList<>();

        @Override
        public void add(Item item) {
            items.add(item);
        }

        @Override
        public List<Item> getItems() {
            return Collections.unmodifiableList(items);
    }
        @Override public int numberOfItems(){
            return items.size();
        }
    }

    static class FixedRule implements PriceRule {
        private final double v;
        FixedRule(double v) {
            this.v = v;
        }
        @Override
        public double priceToAggregate(List<Item> cart){
            return v;
        }
    }

    @Test
    @DisplayName("specification-based")
    void calculateSumsAllRuleOutputs(){
        /* In order of independently of items */
        var cart = new CartStub();
        List<PriceRule> rules = List.of(new FixedRule(5.0), new FixedRule(7.5));
        var amazon = new Amazon(cart, rules);
        cart.add(new Item(ItemType.OTHER, "Pen", 2, 1.25));
        double total = amazon.calculate();

        assertEquals(12.5, total, 1e-9);
    }

    @Test
    @DisplayName("structural-based")
    void addToCartIncreasesCount(){
        /* but empty rules leads to zero total */
        var cart = new CartStub();
        var amazon = new Amazon(cart, List.of());

        assertEquals(0, cart.numberOfItems());
        cart.add(new Item(ItemType.OTHER, "Notebook", 1, 3.0));
        assertEquals(1, cart.numberOfItems());
        assertEquals(0.0, amazon.calculate(),1e-9);
    }

}
