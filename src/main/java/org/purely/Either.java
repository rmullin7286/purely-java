package org.purely;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public sealed interface Either<L, R> {
    default boolean isLeft() {
        return this instanceof Left<L, R>;
    }

    default boolean isRight() {
        return !isLeft();
    }

    default Optional<L> left() {
        return switch (this) {
            case Left(L v) -> Optional.of(v);
            default -> Optional.empty();
        };
    }

    default Optional<R> right() {
        return switch (this) {
            case Right(R v) -> Optional.of(v);
            default -> Optional.empty();
        };
    }

    default Either<R, L> swap() {
        return switch (this) {
            case Left(L v) -> new Right<>(v);
            case Right(R v) -> new Left<>(v);
        };
    }

    default <L2> Either<L2, R> mapLeft(Function<? super L, ? extends L2> mapper) {
        return switch (this) {
            case Left(L v) -> new Left<>(mapper.apply(v));
            case Right<L, R> r -> r.coerce();
        };
    }

    default <R2> Either<L, R2> mapRight(Function<? super R, ? extends R2> mapper) {
        return switch (this) {
            case Left<L, R> l -> l.coerce();
            case Right(R val) -> new Right<>(mapper.apply(val));
        };
    }

    default <L2, R2> Either<L2, R2> mapEither(Function<? super L, ? extends L2> leftMapper,
                                              Function<? super R, ? extends R2> rightMapper) {
        return switch (this) {
            case Left(L v) -> new Left<>(leftMapper.apply(v));
            case Right(R v) -> new Right<>(rightMapper.apply(v));
        };
    }

    default <L2> Either<L2, R> flatMapLeft(Function<? super L, ? extends Either<? extends L2, ? extends R>> mapper) {
        return switch (this) {
            case Left(L v) -> {
                @SuppressWarnings("unchecked") final var ret = (Left<L2, R>)mapper.apply(v);
                yield ret;
            }
            case Right<L, R> r -> r.coerce();
        };
    }

    default <R2> Either<L, R2> flatMapRight(Function<? super R, ? extends Either<? extends L, ? extends R2>> mapper) {
        return switch (this) {
            case Left<L,R> l -> l.coerce();
            case Right(R val) -> {
                @SuppressWarnings("unchecked") final var ret = (Right<L, R2>)mapper.apply(val);
                yield ret;
            }
        };
    }

    default <T> T fold(Function<? super L, ? extends T> leftFunction, Function<? super R, ? extends T> rightFunction) {
        return switch (this) {
            case Left(L v) -> leftFunction.apply(v);
            case Right(R v) -> rightFunction.apply(v);
        };
    }

    default Stream<L> streamLeft() {
        return this.left().stream();
    }

    default Stream<R> streamRight() {
        return this.right().stream();
    }

    default void ifLeft(Consumer<? super L> consumer) {
        this.left().ifPresent(consumer);
    }

    default void ifRight(Consumer<? super R> consumer) {
        this.right().ifPresent(consumer);
    }

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
