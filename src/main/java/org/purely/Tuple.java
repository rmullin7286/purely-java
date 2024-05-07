package org.purely;

/**
 * Tuples are unnamed product types that contain N elements. The Tuple.of() factories can be used to create tuples
 * of various lengths, and the accessor methods first(), second(), third(), fourth(), etc. can be used to access their
 * values.
 * <p>
 * Tuples are useful for one-off return values that don't require their own type to convey clearly what they are. For
 * most cases, you'll want to model your domain as concrete named types.
 */
public final class Tuple {

    public static <A, B> Tuple2<A, B> of(A first, B second) {
        return new Tuple2<>(first, second);
    }

    public static <A, B, C> Tuple3<A, B, C> of(A first, B second, C third) {
        return new Tuple3<>(first, second, third);
    }

    public static <A, B, C, D> Tuple4<A, B, C, D> of(A first, B second, C third, D fourth) {
        return new Tuple4<>(first, second, third, fourth);
    }

    public static <A, B, C, D, E> Tuple5<A, B, C, D, E> of(A first, B second, C third, D fourth, E fifth) {
        return new Tuple5<>(first, second, third, fourth, fifth);
    }

    public static <A, B, C, D, E, F> Tuple6<A, B, C, D, E, F> of(A first,
                                                                 B second,
                                                                 C third,
                                                                 D fourth,
                                                                 E fifth,
                                                                 F sixth) {
        return new Tuple6<>(first, second, third, fourth, fifth, sixth);
    }


    public static <A, B, C, D, E, F, G> Tuple7<A, B, C, D, E, F, G> of(A first,
                                                                       B second,
                                                                       C third,
                                                                       D fourth,
                                                                       E fifth,
                                                                       F sixth,
                                                                       G seventh) {
        return new Tuple7<>(first, second, third, fourth, fifth, sixth, seventh);
    }


    public static <A, B, C, D, E, F, G, H> Tuple8<A, B, C, D, E, F, G, H> of(A first,
                                                                             B second,
                                                                             C third,
                                                                             D fourth,
                                                                             E fifth,
                                                                             F sixth,
                                                                             G seventh,
                                                                             H eighth) {
        return new Tuple8<>(first, second, third, fourth, fifth, sixth, seventh, eighth);
    }

    public static <A, B, C, D, E, F, G, H, I> Tuple9<A, B, C, D, E, F, G, H, I> of(A first,
                                                                                   B second,
                                                                                   C third,
                                                                                   D fourth,
                                                                                   E fifth,
                                                                                   F sixth,
                                                                                   G seventh,
                                                                                   H eighth,
                                                                                   I ninth) {
        return new Tuple9<>(first, second, third, fourth, fifth, sixth, seventh, eighth, ninth);
    }

    public static <A, B, C, D, E, F, G, H, I, J> Tuple10<A, B, C, D, E, F, G, H, I, J> of(A first,
                                                                                          B second,
                                                                                          C third,
                                                                                          D fourth,
                                                                                          E fifth,
                                                                                          F sixth,
                                                                                          G seventh,
                                                                                          H eighth,
                                                                                          I ninth,
                                                                                          J tenth) {
        return new Tuple10<>(first, second, third, fourth, fifth, sixth, seventh, eighth, ninth, tenth);
    }

    public record Tuple2<A, B>(A first, B second) {
    }

    public record Tuple3<A, B, C>(A first, B second, C third) {
    }

    public record Tuple4<A, B, C, D>(A first, B second, C third, D fourth) {
    }

    public record Tuple5<A, B, C, D, E>(A first, B second, C third, D fourth, E fifth) {
    }

    public record Tuple6<A, B, C, D, E, F>(A first, B second, C third, D fourth, E fifth, F sixth) {
    }

    public record Tuple7<A, B, C, D, E, F, G>(A first, B second, C third, D fourth, E fifth, F sixth, G seventh) {
    }

    public record Tuple8<A, B, C, D, E, F, G, H>(A first, B second, C third, D fourth, E fifth, F sixth, G seventh,
                                                 H eighth) {
    }

    public record Tuple9<A, B, C, D, E, F, G, H, I>(A first, B second, C third, D fourth, E fifth, F sixth, G seventh,
                                                    H eighth, I ninth) {
    }

    public record Tuple10<A, B, C, D, E, F, G, H, I, J>(A first, B second, C third, D fourth, E fifth, F sixth,
                                                        G seventh, H eighth, I ninth, J tenth) {
    }

}
