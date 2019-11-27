/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.projectriff.cartprocessor;

import java.util.List;
import java.util.Map;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import static org.junit.Assert.assertEquals;

public class CartProcessorTests {

    @Test
    public void test() {
        CartEvent cartEvent1 = new CartEvent();
        cartEvent1.setUser("homer");
        cartEvent1.setProduct("widget");
        cartEvent1.setQuantity(3);

        CartEvent cartEvent2 = new CartEvent();
        cartEvent2.setUser("homer");
        cartEvent2.setProduct("gadget");
        cartEvent2.setQuantity(7);

        CheckoutEvent checkoutEvent = new CheckoutEvent();
        checkoutEvent.setUser("homer");
    
        Flux<CartEvent> cartEvents = Flux.just(cartEvent1, cartEvent2);
        Flux<CheckoutEvent> checkoutEvents = Flux.just(checkoutEvent);

        CartProcessor processor = new CartProcessor();

        Flux<OrderEvent> orders = processor.apply(Tuples.of(cartEvents, checkoutEvents));
        List<OrderEvent> results = orders.collectList().block();
        OrderEvent order = results.get(0);
        assertEquals("homer", order.getUser());
        Map<String, Integer> products = order.getProducts();
        assertEquals(2, products.size());
        assertEquals(Integer.valueOf(3), products.get("widget"));
        assertEquals(Integer.valueOf(7), products.get("gadget"));
    }
}