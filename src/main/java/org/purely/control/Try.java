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
    /**
     * Runs the given supplier that may throw an exception, and responds with a {@link Try> wrapping the output.
     *
     * @param supplier The supplier to run.
     * @param <T>
     * @return a {@link Success} if the operation returned successfully, or a {@link Failure} containing a {@link Throwable} if it failed.
     */
    static <T> Try<T> of(ThrowingSupplier<T> supplier) {
        Objects.requireNonNull(supplier);
        try {
            return new Success<>(supplier.get());
        } catch (Throwable t) {
            return new Failure<>(Internal.throwIfFatal(t));
        }
    }

    /**
     * Wraps a {@link ThrowingFunction} and returns a {@link Function} that returns a {@link Try}.
     *
     * @param function The function to wrap.
     * @param <T>      The input type.
     * @param <R>      The output type.
     * @return a function that will return {@link Success} if the underlying function succeeds, or {@link Failure} if it throws an exception.
     */
    static <T, R> Function<T, Try<R>> function(ThrowingFunction<T, R> function) {
        return i -> Try.of(() -> function.apply(i));
    }

    /**
     * Tests whether the {@link Try} is a {@link Success}.
     *
     * @return true if the {@link Try} is a {@link Success}, otherwise false.
     */
    default boolean isSuccess() {
        return this instanceof Success<T>;
    }

    /**
     * Tests whether the {@link Try} is a {@link Failure}.
     *
     * @return true if the {@link Try} is a {@link Failure}, otherwise false.
     */
    default boolean isFailure() {
        return !isSuccess();
    }

    /**
     * Retrieves the result of a successful operation, if present.
     *
     * @return an {@link Optional} containing the successful result if present, otherwise an empty {@link Optional}.
     */
    default Optional<T> success() {
        return switch (this) {
            case Success(T v) -> Optional.of(v);
            default -> Optional.empty();
        };
    }

    /**
     * Retrieves the exception thrown if the operation was a failure.
     *
     * @return an {@link Optional} containing the exception if present, otherwise an empty {@link Optional}.
     */
    default Optional<Throwable> failure() {
        return switch (this) {
            case Failure(var t) -> Optional.of(t);
            default -> Optional.empty();
        };
    }

    /**
     * Applies the mapper to a successful result if present, otherwise does nothing. If any non-fatal exception is
     * thrown during the operation, a {@link Failure} will be returned.
     *
     * @param mapper The mapper to apply to the successful value.
     * @param <T2>   The new value type.
     * @return A new {@link Try} with the mapper applied if this is a {@link Success}, otherwise the same {@link Try}.
     */
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

    /**
     * Applies the mapper to the {@link Throwable} if present, otherwise does nothing. if any non-fatal exception is
     * thrown during the mapping and the current {@link Try} is a {@link Failure}, the original {@link Failure} will be
     * returned.
     *
     * @param mapper the mapper to apply to the {@link Try}
     * @return A new {@link} with the mapper applied to the failure case.
     */
    default Try<T> mapFailure(ThrowingFunction<? super Throwable, ? extends Throwable> mapper) {
        Objects.requireNonNull(mapper);
        try {
            return switch (this) {
                case Success<T> s -> s;
                case Failure(var t) -> new Failure<>(mapper.apply(t));
            };
        } catch (Throwable e) {
            Internal.throwIfFatalNoReturn(e);
            return this;
        }
    }

    /**
     * Applies the mapper to the contained value if this is a {@link Success} and returns the result. Otherwise, returns
     * this.
     *
     * @param mapper The mapper to apply.
     * @param <T2>   The new value type.
     * @return Either a new {@link Try} with the mapper applied, or the same {@link Try}
     */
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

    /**
     * Applies the mapper to the contained {@link Throwable} if this is a {@link Failure} and returns the result.
     * Otherwise, returns the same {@link Try}. if any non-fatal exception is thrown during execution and the current
     * {@link Try} is a {@link Failure}, the original {@link Try} will be returned.
     *
     * @param mapper The mapper to apply to the failure case.
     * @return Either a new {@link Try} with the mapper applied, or the same {@link Try}.
     */
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

    /**
     * Applies the predicate to the contained value if a {@link Success}, and maps it to a {@link Failure} using
     * the failureMapper if the predicate evaluates to false. Otherwise, returns the same {@link Try}.
     *
     * @param predicate     predicate to test a condition on a {@link Success} value.
     * @param failureMapper mapper to convert the success value to a {@link Throwable}.
     * @return Either a new {@link Try} if the predicate evaluated to false, or the same {@link Try}.
     */
    default Try<T> filter(Predicate<? super T> predicate, Function<? super T, ? extends Throwable> failureMapper) {
        Objects.requireNonNull(failureMapper);
        return switch (this) {
            case Success(T v) when predicate.test(v) -> this;
            case Success(T v) -> new Failure<>(failureMapper.apply(v));
            case Failure<T> f -> f;
        };
    }

    /**
     * Executes a non-returning operation over the {@link Success} value if present, and returns the same {@link Try}.
     * If a non-fatal exception occurs during the operation, a {@link Failure} will be returned instead, containing that {@link Throwable}.
     *
     * @param consumer The consumer for the success value.
     * @return The same {@link Try}, or a {@link Failure} if an exception occurs.
     */
    default Try<T> execute(ThrowingConsumer<? super T> consumer) {
        Objects.requireNonNull(consumer);
        return this.map(i -> {
            consumer.accept(i);
            return i;
        });
    }

    /**
     * Executes a non-returning operation over the {@link Failure} throwable if present, and returns the same {@link Try}.
     * If a non-fatal exception occurs during the operation occurs, the original {@link Failure} will be returned.
     *
     * @param consumer The consumer for the failure value.
     * @return The same {@link Try}.
     */
    default Try<T> executeOnFailure(ThrowingConsumer<? super Throwable> consumer) {
        Objects.requireNonNull(consumer);
        return this.mapFailure(t -> {
            consumer.accept(t);
            return t;
        });
    }

    /**
     * Retrieve the value if a {@link Success}, or the default value.
     *
     * @param defaultValue The default if this operation is a {@link Failure}.
     * @return Either the contained value, or the default value if not present.
     */
    default T orElse(T defaultValue) {
        Objects.requireNonNull(defaultValue);
        return switch (this) {
            case Success(T v) -> v;
            default -> defaultValue;
        };
    }

    /**
     * Same as {@link #orElse(Object)}, but lazily evaluates the default case using a {@link Supplier}.
     *
     * @param defaultSupplier The supplier for the default value.
     * @return Either the contained value on a successful operation, or the default value.
     * @see #orElse(Object)
     */
    default T orElseGet(Supplier<T> defaultSupplier) {
        Objects.requireNonNull(defaultSupplier);
        return switch (this) {
            case Success(T v) -> v;
            default -> defaultSupplier.get();
        };
    }

    /**
     * Retrieves the value from a successful operation, or throws a {@link NoSuchElementException}.
     *
     * @return The value contained in the {@link Try}.
     */
    default T orElseThrow() {
        return orElseThrow(NoSuchElementException::new);
    }

    /**
     * Retrieves the value from a successful operation, or throws an exception supplied by the exceptionMapper.
     *
     * @param exceptionMapper Receives the {@link Throwable} contained by this {@link Try} and maps it to another
     *                        {@link Throwable} value.
     * @param <E>             The type of the exception to throw.
     * @return The value if this was a successful operation.
     * @throws E If this is a {@link Failure}
     */
    default <E extends Throwable> T orElseThrow(Function<? super Throwable, ? extends E> exceptionMapper) throws E {
        Objects.requireNonNull(exceptionMapper);
        return switch (this) {
            case Success(T v) -> v;
            case Failure(var t) -> throw exceptionMapper.apply(t);
        };
    }

    /**
     * Runs the {@link Consumer} if this is a success.
     *
     * @param consumer Consumes the success value.
     */
    default void ifSuccess(Consumer<? super T> consumer) {
        Objects.requireNonNull(consumer);
        switch (this) {
            case Success(T v) -> consumer.accept(v);
            default -> {
            }
        }
    }

    /**
     * Runs the {@link Consumer} if this is a failure.
     *
     * @param consumer Consumes a {@link Throwable}.
     */
    default void ifFailure(Consumer<? super Throwable> consumer) {
        Objects.requireNonNull(consumer);
        switch (this) {
            case Failure(var t) -> consumer.accept(t);
            default -> {
            }
        }
    }

    /**
     * Represents the success condition of an operation that may respond with an error.
     * @param value The return value of the operation.
     * @param <T> The type of the return value.
     */
    @Pure
    record Success<T>(T value) implements Try<T> {
        public Success {
            Objects.requireNonNull(value);
        }
    }

    /**
     * Represents the failure condition of an operation that may respond with an error.
     * @param throwable The return value of the operation.
     * @param <T> The type of the return value.
     */
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
