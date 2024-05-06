package org.purely;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * An Either represents a value that is one of two possible types, represented by {@link Left} and {@link Right}.
 * This class fully supports switch expression pattern matching, and it may be easier and more readable in some cases
 * to do so rather than using the built-in {@link #mapLeft(Function)}, {@link #mapRight(Function)} etc. suite of
 * functions for transformation.
 * <p>
 * Unlike Scala, this class is not right biased, and therefore does not have the standard map/flatMap monadic
 * methods, but has specialized right, left, or either methods for different cases (e.g. {@link #mapLeft(Function)},
 * {@link #flatMapLeft(Function)}, or {@link #ifLeft(Consumer)} for left values).
 *
 * @param <L> The type represented by {@link Left}
 * @param <R> The type represented by {@link Right}
 */
public sealed interface Either<L, R> {
    /**
     * Tests whether the value represented by the Either is the left value.
     *
     * @return True if the value is the left value, otherwise false.
     */
    default boolean isLeft() {
        return this instanceof Left<L, R>;
    }

    /**
     * Tests if the value represented by the Either is the right value.
     *
     * @return True if the value is the right value, otherwise false.
     */
    default boolean isRight() {
        return !isLeft();
    }

    /**
     * Retrieves the left value as an {@link Optional} if present.
     *
     * @return An {@link Optional} containing the left value, or an empty {@link} Optional
     */
    default Optional<L> left() {
        return switch (this) {
            case Left(L v) -> Optional.of(v);
            default -> Optional.empty();
        };
    }

    /**
     * Retrieves the right value as an {@link Optional} if present.
     *
     * @return An {@link Optional} containing the right value, or an empty {@link Optional}
     */
    default Optional<R> right() {
        return switch (this) {
            case Right(R v) -> Optional.of(v);
            default -> Optional.empty();
        };
    }

    /**
     * Swaps the left and right value of the Either.
     *
     * @return A new Either with the left and right types swapped.
     */
    default Either<R, L> swap() {
        return switch (this) {
            case Left(L v) -> new Right<>(v);
            case Right(R v) -> new Left<>(v);
        };
    }

    /**
     * Creates a new Either with the mapper function applied to the {@link Left}. If the current value is
     * a {@link Right}, the function is not applied and the same value is returned.
     *
     * @param mapper A function that maps a value of L to a value of L2
     * @param <L2>   The new type of the left value.
     * @return A new {@link Left} from applying the function to the old {@link Left}, or the same {@link Either} if the
     * current value is a {@link Right}
     */
    default <L2> Either<L2, R> mapLeft(Function<? super L, ? extends L2> mapper) {
        return switch (this) {
            case Left(L v) -> new Left<>(mapper.apply(v));
            case Right<L, R> r -> r.coerce();
        };
    }

    /**
     * Creates a new {@link Either} with the mapper function applied to the {@link Right}. If the current value is
     * a {@link Left}, the function is not applied and the same value is returned.
     *
     * @param mapper A function that maps a value of R to a value of R2
     * @param <R2>   The new type of the right value.
     * @return A new {@link Right} from applying the function to the old {@link Right}, or the same {@link Either} if the
     * current value is a {@link Left}
     */
    default <R2> Either<L, R2> mapRight(Function<? super R, ? extends R2> mapper) {
        return switch (this) {
            case Left<L, R> l -> l.coerce();
            case Right(R val) -> new Right<>(mapper.apply(val));
        };
    }

    /**
     * Creates a new {@link Left} containing the value returned by the leftMapper applied to the left value
     * if the current {@link Either} is a {@link Left}, otherwise creates a
     * new {@link Right} containing the value returned by the rightMapper applied to the right value.
     *
     * @param leftMapper  A function to transform the left value if present.
     * @param rightMapper A function to transform the right value if present.
     * @param <L2>        The new left type
     * @param <R2>        The new right type
     * @return A new Either with either the left value or the right value updated.
     */
    default <L2, R2> Either<L2, R2> mapEither(Function<? super L, ? extends L2> leftMapper,
                                              Function<? super R, ? extends R2> rightMapper) {
        return switch (this) {
            case Left(L v) -> new Left<>(leftMapper.apply(v));
            case Right(R v) -> new Right<>(rightMapper.apply(v));
        };
    }

    /**
     * Applies the value contained by a {@link Left} and returns the result of the function, or returns the same
     * value if this is a {@link Right}
     *
     * @param mapper The function to apply to the left value.
     * @param <L2>   The new left type.
     * @return A new Either of the mapper applied to the {@link Left}'s value, or the same {@link Right}.
     */
    default <L2> Either<L2, R> flatMapLeft(Function<? super L, ? extends Either<? extends L2, ? extends R>> mapper) {
        return switch (this) {
            case Left(L v) -> {
                @SuppressWarnings("unchecked") final var ret = (Left<L2, R>) mapper.apply(v);
                yield ret;
            }
            case Right<L, R> r -> r.coerce();
        };
    }

    /**
     * Applies the value contained by a {@link Right} and returns the result of the function, or returns the same
     * value if this is a {@link Right}
     *
     * @param mapper The function to apply to the Right value.
     * @param <R2>   The new right type.
     * @return A new Either of the mapper applied to the {@link Right}'s value, or the same {@link Left}.
     */
    default <R2> Either<L, R2> flatMapRight(Function<? super R, ? extends Either<? extends L, ? extends R2>> mapper) {
        return switch (this) {
            case Left<L, R> l -> l.coerce();
            case Right(R val) -> {
                @SuppressWarnings("unchecked") final var ret = (Right<L, R2>) mapper.apply(val);
                yield ret;
            }
        };
    }

    /**
     * Collapses both {@link Left} and {@link Right} cases into a single value using the leftFunction if this is
     * a {@link Left} or the rightFunction if this is a {@link Right}.
     *
     * @param leftFunction  The function to be applied over the left value if present.
     * @param rightFunction The function to be applied over the right value if present.
     * @param <T>           The type of the new value.
     * @return A new value resulting from applying one of the two functions.
     */
    default <T> T fold(Function<? super L, ? extends T> leftFunction, Function<? super R, ? extends T> rightFunction) {
        return switch (this) {
            case Left(L v) -> leftFunction.apply(v);
            case Right(R v) -> rightFunction.apply(v);
        };
    }

    /**
     * If the current value is a {@link Left}, this method will create a {@link Stream} containing the left value.
     * This is useful for {@link Stream} computations where you want to only stream over left values and discard
     * right values, such as:
     * <pre>{@code
     * final int sum = List<Either<Integer,String>> list = List.of(
     *     new Left<>("foo"),
     *     new Right<>(1),
     *     new Left<>("bar"),
     *     new Right<>(2),
     *     new Left<>("baz");
     * );
     * final int sum = list.stream()
     *     .flatMapToInt(Either::streamLeft)
     *     .sum();
     * }</pre>
     *
     * @return A stream containing the left value if present, or an empty stream.
     */
    default Stream<L> streamLeft() {
        return this.left().stream();
    }


    /**
     * If the current value is a {@link Right}, this method will create a {@link Stream} containing the right value.
     * This is useful for {@link Stream} computations where you want to only stream over right values and discard
     * left values, such as:
     * <pre>{@code
     * final int sum = List<Either<Integer,String>> list = List.of(
     *     new Left<>("foo"),
     *     new Right<>(1),
     *     new Left<>("bar"),
     *     new Right<>(2),
     *     new Left<>("baz");
     * );
     * final String joined = list.stream()
     *     .flatMap(Either::streamRight)
     *     .collect(Collectors.joining(","));
     *
     * }</pre>
     *
     * @return A stream containing the left value if present, or an empty stream.
     */
    default Stream<R> streamRight() {
        return this.right().stream();
    }

    /**
     * Runs the {@link Consumer} over the left value if present, or does nothing.
     *
     * @param consumer The consumer to run if the left value is present.
     */
    default void ifLeft(Consumer<? super L> consumer) {
        this.left().ifPresent(consumer);
    }

    /**
     * Runs the {@link Consumer} over the right value if present, or does nothing.
     *
     * @param consumer The consumer to run if the right value is present.
     */
    default void ifRight(Consumer<? super R> consumer) {
        this.right().ifPresent(consumer);
    }

    /**
     * This record represents an {@link Either} that currently contains a value of the left type L.
     *
     * @param value The left value.
     * @param <L>
     * @param <R>
     */
    record Left<L, R>(L value) implements Either<L, R> {
        @SuppressWarnings("unchecked")
        private <R2> Either<L, R2> coerce() {
            return (Either<L, R2>) this;
        }
    }

    record Right<L, R>(R value) implements Either<L, R> {
        @SuppressWarnings("unchecked")
        private <L2> Either<L2, R> coerce() {
            return (Either<L2, R>) this;
        }
    }
}
