/*
 * Copyright 2017 Shinya Mochida
 * 
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,software
 * Distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mikeneck.util;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PairTest {

    @Test
    void map() {
        final Pair<String, String> pair = new Pair<>("foo", "bar");
        final Pair<String, String> actual = pair.map(String::toUpperCase);
        assertEquals(new Pair<>("foo", "BAR"), actual);
    }

    @Test
    void bimap() {
        final Function<String, String> TO_UPPER_CASE = String::toUpperCase;
        final Pair<Function<String, String>, String> actual = new Pair<>(TO_UPPER_CASE, "foo").bimap(Function::apply);
        assertEquals(new Pair<>(TO_UPPER_CASE, "FOO"), actual);
    }

    @Test
    void reverse() {
        final Pair<String, Long> actual = new Pair<>(1L, "foo").reverse();
        assertEquals(new Pair<>("foo", 1L), actual);
    }

    @Test
    void consume() {
        final StringBuilder sb = new StringBuilder();
        new Pair<>(1, "foo").consumeLeft(sb::append).consumeRight(sb::append);
        assertEquals("1foo", sb.toString());
    }

    @Test
    void mkPair() {
        final Pair<String, Integer> pair = Optional.of("foo")
                .map(Pair.mkPair(String::length))
                .orElseThrow(IllegalStateException::new);
        assertEquals(new Pair<>("foo", 3), pair);
    }

    @Test
    void mapPair() {
        final Pair<String, String> pair = Optional.of("foo")
                .map(Pair.mkPair(String::length))
                .map(Pair.mapPair(n -> n * n * n))
                .map(Pair.mapPair(Integer::toHexString))
                .orElseThrow(IllegalStateException::new);
        assertEquals(new Pair<>("foo", "1b"), pair);
    }

    @Test
    void bimapPair() {
        final Function<Pair<Integer, Integer>, Pair<Integer, Integer>> fibo = p -> new Pair<>(p.getRight(), p.getLeft() + p.getRight());
        final Pair<Integer, Integer> pair = Optional.of(new Pair<>(fibo, new Pair<>(1, 1)))
                .map(Pair.bimapPair(Function::apply))
                .map(Pair.bimapPair(Function::apply))
                .map(Pair.bimapPair(Function::apply))
                .map(Pair.bimapPair(Function::apply))
                .orElseThrow(IllegalStateException::new)
                .getRight();
        assertEquals(new Pair<>(5, 8), pair);
    }
}
