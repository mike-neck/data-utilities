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

public interface Last<T> extends Monoid<T, Last<T>> {

    boolean isEmpty();

    @NotNull
    @Contract("null->fail")
    <R> Last<R> map(@NotNull Function<? super T, ? extends R> function);

    @NotNull
    @Contract("null->fail")
    T or(@NotNull Supplier<? extends T> candidate);

    @NotNull
    @Contract("null->fail")
    Last<T> append(@NotNull Supplier<? extends Optional<T>> candidate);

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @NotNull
    @Contract("null->fail")
    Last<T> append(@NotNull Optional<T> candidate);

    @NotNull
    @Contract("null->fail")
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    static <T> Last<T> of(@NotNull @NonNull Optional<T> candidate) {
        return candidate.<Last<T>>map(Candidate::new)
                .orElseGet(Yet::new);
    }

    @NotNull
    @Contract("null->!null;_->!null")
    static <T> Last<T> of(@Nullable T nullable) {
        return of(Optional.ofNullable(nullable));
    }
}

class Candidate<T> implements Last<T> {

    private final T value;

    Candidate(@NotNull T value) {
        this.value = value;
    }

    @Override
    public @NotNull Last<T> append(@NotNull @NonNull Last<T> other) {
        //noinspection Contract
        if (other.isEmpty()) {
            return this;
        }
        return other;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @NotNull
    @Override
    public <R> Last<R> map(@NotNull @NonNull Function<? super T, ? extends R> function) {
        return new Candidate<>(function.apply(value));
    }

    @NotNull
    @Override
    public T or(@NotNull @NonNull Supplier<? extends T> candidate) {
        return candidate.get();
    }

    @NotNull
    @Override
    public Last<T> append(@NotNull @NonNull Supplier<? extends Optional<T>> candidate) {
        return candidate.get()
                .<Last<T>>map(Candidate::new)
                .orElse(this);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @NotNull
    @Override
    public Last<T> append(@NotNull @NonNull Optional<T> candidate) {
        return candidate.<Last<T>>map(Candidate::new)
                .orElse(this);
    }
}

class Yet<T> implements Last<T> {

    @Override
    public @NotNull Last<T> append(@NotNull @NonNull Last<T> other) {
        //noinspection Contract
        return other;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @NotNull
    @Override
    public <R> Last<R> map(@NotNull @NonNull Function<? super T, ? extends R> function) {
        return new Yet<>();
    }

    @NotNull
    @Override
    public T or(@NotNull @NonNull Supplier<? extends T> candidate) {
        return candidate.get();
    }

    @NotNull
    @Override
    public Last<T> append(@NotNull @NonNull Supplier<? extends Optional<T>> candidate) {
        return candidate.get()
                .<Last<T>>map(Candidate::new)
                .orElse(this);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @NotNull
    @Override
    public Last<T> append(@NotNull @NonNull Optional<T> candidate) {
        return candidate.<Last<T>>map(Candidate::new)
                .orElse(this);
    }
}
