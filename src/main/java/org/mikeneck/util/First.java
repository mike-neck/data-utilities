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

import lombok.NonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface First<T> extends Monoid<T, First<T>> {

    boolean isEmpty();

    @NotNull
    @Contract("null->fail")
    <R> First<R> map(@NotNull Function<? super T, ? extends R> function);

    @NotNull
    @Contract("null->fail")
    T or(@NotNull Supplier<? extends T> candidate);

    @NotNull
    @Contract("null->fail")
    First<T> append(@NotNull Supplier<? extends Optional<T>> candidate);

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @NotNull
    @Contract("null->fail")
    First<T> append(@NotNull Optional<T> candidate);

    @NotNull
    @Contract("null->fail")
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    static <T> First<T> of(@NotNull @NonNull Optional<T> candidate) {
        return candidate.<First<T>>map(Already::new)
                .orElseGet(Already.Empty::new);
    }

    @NotNull
    @Contract("null->!null;_->!null")
    static <T> First<T> of(@Nullable T nullable) {
        return of(Optional.ofNullable(nullable));
    }

    @NotNull
    @Contract(" -> !null")
    static <T> First<T> empty() {
        return new Already.Empty<>();
    }
}

class Already<T> implements First<T> {

    private final T value;

    Already(T value) {
        this.value = value;
    }

    @Override
    public @NotNull First<T> append(@NotNull @NonNull First<T> other) {
        //noinspection Contract
        return this;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @NotNull
    @Override
    public <R> First<R> map(@NotNull @NonNull Function<? super T, ? extends R> function) {
        return new Already<>(function.apply(value));
    }

    @NotNull
    @Override
    public T or(@NotNull @NonNull Supplier<? extends T> candidate) {
        //noinspection Contract
        return value;
    }

    @NotNull
    @Override
    public First<T> append(@NotNull @NonNull Supplier<? extends Optional<T>> candidate) {
        //noinspection Contract
        return this;
    }

    @NotNull
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Override
    public First<T> append(@NotNull @NonNull Optional<T> candidate) {
        //noinspection Contract
        return this;
    }

    static class Empty<T> implements First<T> {
    
        @Override
        public @NotNull First<T> append(@NotNull @NonNull First<T> other) {
            //noinspection Contract
            return other;
        }
    
        @Override
        public boolean isEmpty() {
            return true;
        }
    
        @NotNull
        @Override
        public <R> First<R> map(@NotNull @NonNull Function<? super T, ? extends R> function) {
            return new Empty<>();
        }
    
        @NotNull
        @Override
        public T or(@NotNull @NonNull Supplier<? extends T> candidate) {
            return candidate.get();
        }
    
        @NotNull
        @Override
        public First<T> append(@NotNull @NonNull Supplier<? extends Optional<T>> candidate) {
            return candidate.get()
                    .<First<T>>map(Already::new)
                    .orElse(this);
        }
    
        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        @NotNull
        @Override
        public First<T> append(@NotNull @NonNull Optional<T> candidate) {
            return candidate.<First<T>>map(Already::new)
                    .orElse(this);
        }
    }
}

