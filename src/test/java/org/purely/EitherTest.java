package org.purely;

import org.junit.jupiter.api.Test;
import org.purely.Either.Left;
import org.purely.Either.Right;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class EitherTest {

    @Test
    void isLeft() {
        assertTrue(new Left<>(1).isLeft());
        assertFalse(new Right<>("foo").isLeft());
    }

    @Test
    void isRight() {
        assertTrue(new Right<>("foo").isRight());
        assertFalse(new Left<>(1).isRight());
    }

    @Test
    void left() {
        assertEquals(Optional.of(1), new Left<>(1).left());
        assertEquals(Optional.empty(), new Right<>("foo").left());
    }

    @Test
    void right() {
        assertEquals(Optional.of("foo"), new Right<>("foo").right());
        assertEquals(Optional.empty(), new Left<>(1).right());
    }

    @Test
    void swap() {
        assertEquals(new Right<>(1), new Left<>(1).swap());
        assertEquals(new Left<>("foo"), new Right<>("foo").swap());
    }

    @Test
    void mapLeft() {
        assertEquals(new Left<>(2), new Left<>(1).mapLeft(i -> i + 1));
        final Either<Integer, String> right = new Right<>("foo");
        assertSame(right, right.mapLeft(i -> i + 1));
    }

    @Test
    void mapRight() {
        assertEquals(new Right<>("foo!"), new Right<>("foo").mapRight(i -> i + "!"));
        final Either<Integer, String> left = new Left<>(1);
        assertSame(left, left.mapRight(i -> i + "!"));
    }

    @Test
    void mapEither() {
        assertEquals(new Left<>(2), new Left<Integer, String>(1).mapEither(
                i -> i + 1,
                i -> i + "!"
        ));
        assertEquals(new Right<>("foo!"), new Right<Integer, String>("foo").mapEither(
                i -> i + 1,
                i -> i + "!"
        ));
    }

    @Test
    void flatMapLeft() {
        assertEquals(new Left<>(2), new Left<>(1).flatMapLeft(i -> new Left<>(i + 1)));
        final Either<Integer, String> right = new Right<>("foo");
        assertSame(right, right.flatMapLeft(i -> new Left<>(i + 1)));
    }

    @Test
    void flatMapRight() {
        assertEquals(new Right<>("foo!"), new Right<>("foo").flatMapRight(i -> new Right<>(i + "!")));
        final Either<Integer, String> left = new Left<>(1);
        assertSame(left, left.flatMapRight(i -> new Right<>(i + 1)));
    }

    @Test
    void fold() {
        final long res1 = new Left<Integer, String>(1).fold(
                i -> (long)i,
                Long::parseLong
        );
        final long res2 = new Right<Integer, String>("1").fold(
                i -> (long)i,
                Long::parseLong
        );
        assertEquals(1L, res1);
        assertEquals(1L, res2);
    }

    @Test
    void streamLeft() {
        final List<Either<Integer, String>> input = List.of(
                new Left<>(1),
                new Right<>("foo"),
                new Left<>(2),
                new Right<>("bar"),
                new Left<>(3),
                new Right<>("baz")
        );
        assertEquals(Optional.of(6), input.stream().flatMap(Either::streamLeft).reduce(Integer::sum));
    }

    @Test
    void streamRight() {
        final List<Either<Integer, String>> input = List.of(
                new Left<>(1),
                new Right<>("foo"),
                new Left<>(2),
                new Right<>("bar"),
                new Left<>(3),
                new Right<>("baz")
        );
        assertEquals("foo,bar,baz", input.stream().flatMap(Either::streamRight).collect(Collectors.joining(",")));
    }

    @Test
    void ifLeft() {
        final AtomicBoolean flag = new AtomicBoolean(false);
        new Left<>(1).ifLeft( __ -> flag.set(true));
        new Right<>("foo").ifLeft(__ -> fail("ifLeft should never be called on a Right"));
        assertTrue(flag.get());
    }

    @Test
    void ifRight() {
        final AtomicBoolean flag = new AtomicBoolean(false);
        new Right<>("foo").ifRight(__ -> flag.set(true));
        new Left<>(1).ifRight(__ -> fail("ifRight should never be called on a Left"));
        assertTrue(flag.get());
    }
}