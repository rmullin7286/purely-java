package org.purely.control;

import org.junit.jupiter.api.Test;
import org.purely.control.Either.Left;
import org.purely.control.Either.Right;
import org.purely.control.Try.Failure;
import org.purely.control.Try.Success;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class TryTest {

    @Test
    void of() {
        assertThrows(VirtualMachineError.class, () -> Try.of(() -> { throw new OutOfMemoryError(); }));
        assertThrows(LinkageError.class, () -> Try.of(() -> { throw new LinkageError(); }));
        assertErrorOf(NumberFormatException.class, Try.of(() -> Integer.parseInt("foo")));
        assertSuccess(1, Try.of(() -> Integer.parseInt("1")));
    }

    @Test
    void function() {
        assertThrows(VirtualMachineError.class, () -> Try.function(__ -> { throw new OutOfMemoryError(); }).apply(1));
        assertThrows(LinkageError.class, () -> Try.function(__ -> { throw new LinkageError(); }).apply(1));
        assertErrorOf(NumberFormatException.class, Try.<String, Integer>function(Integer::parseInt).apply("foo"));
        assertSuccess(1, Try.<String, Integer>function(Integer::parseInt).apply("1"));
    }

    @Test
    void fromEither() {
        assertSuccess(1, Try.fromEither(new Right<>(1)));
        assertErrorOf(RuntimeException.class, Try.fromEither(new Left<>(new RuntimeException())));
    }

    @Test
    void isSuccess() {
        assertTrue(new Success<>(1).isSuccess());
        assertFalse(new Failure<>(new RuntimeException()).isSuccess());
    }

    @Test
    void isFailure() {
        assertTrue(new Failure<>(new RuntimeException()).isFailure());
        assertFalse(new Success<>(1).isFailure());
    }

    @Test
    void success() {
        assertEquals(Optional.of(1), new Success<>(1).success());
        assertEquals(Optional.empty(), new Failure<>(new RuntimeException()).success());
    }

    @Test
    void failure() {
        assertEquals(Optional.empty(), new Success<>(1).failure());
        assertTrue(new Failure<>(new RuntimeException()).failure().isPresent());
    }

    @Test
    void map() {
        assertSuccess(2, new Success<Integer>(1).map(i -> i + 1));
        assertErrorOf(NumberFormatException.class, new Success<>("foo").map(Integer::parseInt));
        assertErrorOf(IllegalStateException.class, new Failure<Integer>(new IllegalStateException()).map(i -> i + 1));
    }

    @Test
    void mapFailure() {
        assertErrorOf(NoSuchElementException.class, new Failure<>( new IllegalStateException() ).mapFailure(NoSuchElementException::new));
        assertSuccess(1, new Success<Integer>(1).mapFailure(NoSuchElementException::new));
    }

    @Test
    void flatMap() {
        assertSuccess(1, new Success<>("1").flatMap(i -> Try.of(() -> Integer.parseInt(i))));
        assertErrorOf(NoSuchElementException.class, new Failure<String>(new NoSuchElementException()).flatMap(i -> Try.of(() -> Integer.parseInt(i))));
        assertErrorOf(NumberFormatException.class, new Success<>("foo").flatMap(i -> Try.of(() -> Integer.parseInt(i))));
    }

    @Test
    void flatMapFailure() {
        assertErrorOf(IllegalStateException.class, new Failure<>(new RuntimeException()).flatMapFailure(i -> new Failure<>(new IllegalStateException(i))));
    }

    @Test
    void filter() {
        assertErrorOf(IllegalStateException.class, new Success<>(2).filter(i -> i != 2, i -> new IllegalStateException(i.toString())));
        assertSuccess(1, new Success<>(1).filter(i -> i != 2, i -> new IllegalStateException(i.toString())));
    }

    @Test
    void execute() {
        AtomicInteger test = new AtomicInteger(0);
        new Success<>(1).execute(test::set);
        assertEquals(1, test.get());
        assertErrorOf(IllegalStateException.class, new Success<>(1).execute(__ -> { throw new IllegalStateException(); }));
    }

    @Test
    void executeOnFailure() {
        AtomicInteger test = new AtomicInteger(0);
        new Failure<>(new RuntimeException()).executeOnFailure(__ -> test.set(1));
        assertEquals(1, test.get());
    }

    @Test
    void orElse() {
        assertEquals(1, new Success<>(1).orElse(2));
        assertEquals(2, new Failure<>(new RuntimeException()).orElse(2));
    }

    @Test
    void orElseGet() {
        assertEquals(1, new Success<>(1).orElseGet(() -> 2));
        assertEquals(2, new Failure<>(new RuntimeException()).orElseGet(() -> 2));
    }

    @Test
    void orElseThrow() {
        assertThrows(NoSuchElementException.class, () -> new Failure<>(new RuntimeException()).orElseThrow());
        assertEquals(1, new Success<>(1).orElseThrow());
    }

    @Test
    void testOrElseThrow() {
        assertThrows(IllegalStateException.class, () -> new Failure<>(new RuntimeException()).orElseThrow(IllegalStateException::new));
        assertEquals(1, new Success<>(1).orElseThrow(IllegalStateException::new));
    }

    @Test
    void ifSuccess() {
        AtomicInteger test = new AtomicInteger(1);
        new Success<>(2).ifSuccess(test::set);
        new Failure<>(new RuntimeException()).ifSuccess(i -> test.set(3));
        assertEquals(2, test.get());
    }

    @Test
    void ifFailure() {
        AtomicInteger test = new AtomicInteger(1);
        new Success<>(2).ifFailure(i -> test.set(2));
        new Failure<>(new RuntimeException()).ifFailure(i -> test.set(3));
        assertEquals(3, test.get());
    }

    @Test
    void toEither() {
        assertTrue(new Failure<>(new RuntimeException()).toEither().isLeft());
        assertEquals(new Right<>(1), new Success<>(1).toEither());
    }

    <T> void assertErrorOf(Class<? extends Throwable> clazz, Try<T> value ) {
        switch (value) {
            case Failure(var e) when clazz.isInstance(e) -> {}
            default -> fail("%s does not represent %s".formatted(value, clazz));
        }
    }

    <T> void assertSuccess(T expected, Try<T> actual) {
        switch (actual) {
            case Success(var v) -> {}
            default -> fail("Operation failed with %s".formatted(actual));
        }
    }
}