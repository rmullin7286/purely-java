package org.purely.control;

/**
 * package private helper methods
 */
final class Internal {
    @SuppressWarnings("unchecked")
    static <L,R> Either<L,R> narrow(Either<? extends L, ? extends R> either) {
        return (Either<L, R>)either;
    }

    @SuppressWarnings("unchecked")
    static <T> Try<T> narrow(Try<? extends T> t) {
        return (Try<T>)t;
    }

    @SuppressWarnings("unchecked")
    static <E extends Throwable> E sneakyThrow(Throwable e) throws E {
        throw (E) e;
    }

    static Throwable throwIfFatal(Throwable t) {
        return switch (t) {
            case LinkageError e -> throw e;
            case VirtualMachineError e -> throw e;
            case InterruptedException e -> throw sneakyThrow(e);
            default -> t;
        };
    }

    @SuppressWarnings({"ThrowableNotThrown", "ResultOfMethodCallIgnored"})
    static void throwIfFatalNoReturn(Throwable t) {
        throwIfFatal(t);
    }
}
