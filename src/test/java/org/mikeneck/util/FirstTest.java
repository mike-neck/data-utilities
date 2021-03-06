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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

public class FirstTest {

    @Nested
    class InstanceCreation {

        @Test
        void already() {
            final String string = First.of(Optional.of("already")).or(() -> "yet");
            assertEquals("already", string);
        }

        @Test
        void yet() {
            final String string = First.of(Optional.<String>empty()).or(() -> "yet");
            assertEquals("yet", string);
        }
    }

    @Nested
    class Append {

        private final Supplier<String> fail = () -> {
            Assertions.fail("this code should not be executed");
            return "";
        };

        @Nested
        class AlreadyTest {

            private final First<String> first = First.of(Optional.of("test"));

            @Test
            void appendNotEmpty() {
                final String actual = first.append(Optional.of("not empty"))
                        .or(fail);
                assertEquals("test", actual);
            }

            @Test
            void appendEmpty() {
                final String actual = first.append(Optional.empty())
                        .or(fail);
                assertEquals("test", actual);
            }

            @Test
            void appendSupplierNotEmpty() {
                final String actual = first.append(() -> Optional.of("supplier"))
                        .or(fail);
                assertEquals("test", actual);
            }

            @Test
            void appendSupplierEmpty() {
                final String actual = first.append(Optional::empty)
                        .or(fail);
                assertEquals("test", actual);
            }
        }

        @Nested
        class EmptyTest {

            private final First<String> first = First.empty();

            @Test
            void appendNotEmpty() {
                final String actual = first.append(Optional.of("not empty"))
                        .or(fail);
                assertEquals("not empty", actual);
            }

            @Test
            void appendEmpty() {
                final String actual = first.append(Optional.empty())
                        .or(() -> "empty");
                assertEquals("empty", actual);
            }

            @Test
            void appendSupplierNotEmpty() {
                final String actual = first.append(() -> Optional.of("not empty"))
                        .or(fail);
                assertEquals("not empty", actual);
            }

            @Test
            void appendSupplerEmpty() {
                final String actual = first.append(Optional::empty)
                        .or(() -> "empty");
                assertEquals("empty", actual);
            }
        }

        @Test
        void alreadyAppendEmptyReturnsAlready() {
            final First<String> already = First.of("first");
            final First<String> empty = First.empty();

            final First<String> actual = already.append(empty);
            assertAll(
                    () -> assertFalse(actual.isEmpty()),
                    () -> assertEquals("first", actual.or(fail))
            );
        }

        @Test
        void alreadyAppendAlreadyReturnsFirstAlready() {
            final First<String> first = First.of("first");
            final First<String> second = First.of("second");
            final String actual = first.append(second).or(fail);
            assertAll(
                    () -> assertFalse(actual.isEmpty()),
                    () -> assertEquals("first", actual)
            );
        }

        @Test
        void emptyAppendAlreadyReturnsAlready() {
            final First<String> empty = First.empty();
            final First<String> already = First.of("first");

            final First<String> actual = empty.append(already);
            assertAll(
                    () -> assertFalse(actual.isEmpty()),
                    () -> assertEquals("first", actual.or(fail))
            );
        }

        @Test
        void emptyAppendEmptyReturnsEmpty() {
            final First<Object> first = First.empty();
            final First<Object> second = First.empty();

            final First<Object> actual = first.append(second);
            assertAll(
                    () -> assertTrue(actual.isEmpty()),
                    () -> assertEquals("empty", actual.or(() -> "empty"))
            );
        }
    }

    @Nested
    class Map {

        private final Supplier<String> fail = () -> {
            Assertions.fail("this code should not be executed");
            return "";
        };

        @Test
        void mapAlreadyReturnsMappedValue() {
            final First<String> first = First.of("test-code");
            final First<String> actual = first.map(String::toUpperCase);
            assertAll(
                    () -> assertFalse(actual.isEmpty()),
                    () -> assertEquals("TEST-CODE", actual.or(fail))
            );
        }

        @Test
        void mapEmptyReturnsEmpty() {
            final First<Object> empty = First.empty();
            final First<String> actual = empty.map(Object::toString);

            assertAll(
                    () -> assertTrue(actual.isEmpty()),
                    () -> assertEquals("empty", actual.or(() -> "empty"))
            );
        }
    }
}
