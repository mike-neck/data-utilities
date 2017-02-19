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

import lombok.Data;
import lombok.NonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@Data
public class Pair<L, R> {

    private final L left;
    private final R right;

    public Pair(@NotNull @NonNull L left, @NotNull @NonNull R right) {
        this.left = left;
        this.right = right;
    }

    @NotNull
    @Contract("null->fail")
    public <N> Pair<L, N> map(@NotNull @NonNull Function<? super R, ? extends N> function) {
        return new Pair<>(left, function.apply(right));
    }

    @NotNull
    @Contract("null->fail")
    public <N> Pair<L, N> bimap(@NotNull @NonNull BiFunction<? super L, ? super R, ? extends N> function) {
        return new Pair<>(left, function.apply(left, right));
    }

    @NotNull
    public Pair<R, L> reverse() {
        return new Pair<>(right, left);
    }

    @NotNull
    @Contract("null->fail")
    public ConsumeRight<R> consumeLeft(@NotNull @NonNull Consumer<? super L> cl) {
        //noinspection Contract
        return cr -> {
            Objects.requireNonNull(cr);
            cl.accept(left);
            cr.accept(right);
        };
    }

    @FunctionalInterface
    public interface ConsumeRight<R> {
        @Contract("null->fail")
        void consumeRight(@NotNull Consumer<? super R> cr);
    }

    @NotNull
    @Contract("null->fail")
    public static <L, R> Function<L, Pair<L, R>> mkPair(@NotNull @NonNull Function<? super L, ? extends R> function) {
        //noinspection Contract
        return l -> new Pair<>(l, function.apply(l));
    }

    @NotNull
    @Contract("null->fail")
    public static <L, R, N> Function<Pair<L, R>, Pair<L, N>> mapPair(@NotNull @NonNull Function<? super R, ? extends N> function) {
        //noinspection Contract
        return p -> new Pair<>(p.left, function.apply(p.right));
    }

    @NotNull
    @Contract("null->fail")
    public static <L, R, N> Function<Pair<L, R>, Pair<L, N>> bimapPair(@NotNull @NonNull BiFunction<? super L, ? super R, ? extends N> function) {
        //noinspection Contract
        return p -> p.bimap(function);
    }
}
