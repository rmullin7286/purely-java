package org.purely.control;

import org.purely.annotations.Pure;
import org.purely.functions.ThrowingConsumer;
import org.purely.functions.ThrowingFunction;
import org.purely.functions.ThrowingSupplier;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Try can be thought of as a specialization for {@link Either}, where the {@link org.purely.control.Either.Left} is
 * a Throwable value, and the {@link org.purely.control.Either.Right} is the result of an operation that might throw.
 * <p>
 * This class will not handle any exception that is considered a fatal error, which includes
 * <ul>
 *    <li>{@link LinkageError}</li>
 *    <li>{@link VirtualMachineError}</li>
 *    <li>{@link InterruptedException}</li>
 * </ul>
 * This class is intended to be useful for running operations in Java lambda expressions, such as those on
 * {@link java.util.stream.Stream} where checked exceptions cannot be thrown. It's up to you and your team whether you
 * want to replace all standard Java exception handling with Try.
 */
@Pure
public sealed interface Try<T> {
    static <T> Try<T> of(ThrowingSupplier<T> supplier) {
        Objects.requireNonNull(supplier);
        try {
            return new Success<>(supplier.get());
        } catch (Throwable t) {
            return new Failure<>(Internal.throwIfFatal(t));
        }
    }

    default boolean isSuccess() {
        return this instanceof Success<T>;
    }

    default boolean isFailure() {
        return !isSuccess();
    }

    default Optional<T> success() {
        return switch (this) {
            case Success(T v) -> Optional.of(v);
            default -> Optional.empty();
        };
    }

    default Optional<Throwable> failure() {
        return switch (this) {
            case Failure(var t) -> Optional.of(t);
            default -> Optional.empty();
        };
    }

    default <T2> Try<T2> map(ThrowingFunction<? super T, ? extends T2> mapper) {
        Objects.requireNonNull(mapper);
        try {
            return switch (this) {
                case Success(T v) -> new Success<>(mapper.apply(v));
                case Failure<T> f -> f.coerce();
            };
        } catch (Throwable t) {
            return new Failure<>(Internal.throwIfFatal(t));
        }
    }

    default Try<T> mapFailure(ThrowingFunction<? super Throwable, ? extends Throwable> mapper) {
        Objects.requireNonNull(mapper);
        try {
            return switch (this) {
                case Success<T> s -> s;
                case Failure(var t) -> new Failure<>(mapper.apply(t));
            };
        } catch (Throwable e) {
            return new Failure<>(Internal.throwIfFatal(e));
        }
    }

    default <T2> Try<T2> flatMap(ThrowingFunction<? super T, ? extends Try<? extends T2>> mapper) {
        Objects.requireNonNull(mapper);
        try {
            return switch (this) {
                case Success(T v) -> Internal.narrow(mapper.apply(v));
                case Failure<T> f -> f.coerce();
            };
        } catch (Throwable t) {
            return new Failure<>(Internal.throwIfFatal(t));
        }
    }

    default Try<T> flatMapFailure(ThrowingFunction<? super Throwable, ? extends Try<? extends T>> mapper) {
        Objects.requireNonNull(mapper);
        try {
            return switch (this) {
                case Success<T> s -> s;
                case Failure(var t) -> Internal.narrow(mapper.apply(t));
            };
        } catch (Throwable t) {
            return new Failure<>(Internal.throwIfFatal(t));
        }
    }

    default Try<T> filter(Predicate<? super T> predicate, Function<? super T, ? extends Throwable> failureMapper) {
        Objects.requireNonNull(failureMapper);
       return switch (this) {
           case Success(T v) when predicate.test(v) -> this;
           case Success(T v) -> new Failure<>(failureMapper.apply(v));
           case Failure<T> f -> f;
       };
    }

    default Try<T> execute(ThrowingConsumer<? super T> consumer) {
        Objects.requireNonNull(consumer);
        return this.map(i -> {
            consumer.accept(i);
            return i;
        });
    }

    default Try<T> executeOnFailure(ThrowingConsumer<? super Throwable> consumer) {
        Objects.requireNonNull(consumer);
        return this.mapFailure(t -> {
            consumer.accept(t);
            return t;
        });
    }

    default T orElse(T defaultValue) {
        Objects.requireNonNull(defaultValue);
        return switch (this) {
            case Success(T v) -> v;
            default -> defaultValue;
        };
    }

    default T orElseGet(Supplier<T> defaultSupplier) {
        Objects.requireNonNull(defaultSupplier);
        return switch (this) {
            case Success(T v) -> v;
            default -> defaultSupplier.get();
        };
    }

    default T orElseThrow() {
        return orElseThrow(NoSuchElementException::new);
    }

    default <E extends Throwable> T orElseThrow(Function<? super Throwable, ? extends E> exceptionMapper) throws E {
        Objects.requireNonNull(exceptionMapper);
        return switch (this) {
            case Success(T v) -> v;
            case Failure(var t) -> throw exceptionMapper.apply(t);
        };
    }

    default void ifSuccess(Consumer<? super T> consumer) {
        Objects.requireNonNull(consumer);
        switch (this) {
            case Success(T v) -> consumer.accept(v);
            default -> {}
        }
    }

    default void ifFailure(Consumer<? super Throwable> consumer) {
        Objects.requireNonNull(consumer);
        switch (this) {
            case Failure(var t) -> consumer.accept(t);
            default -> {}
        }
    }

    @Pure
    record Success<T>(T value) implements Try<T> {
        public Success {
            Objects.requireNonNull(value);
        }
    }

    @Pure
    record Failure<T>(Throwable throwable) implements Try<T> {
        public Failure {
            Objects.requireNonNull(throwable);
        }

        @SuppressWarnings("unchecked")
        private <T2> Failure<T2> coerce() {
            return (Failure<T2>) this;
        }
    }
}
