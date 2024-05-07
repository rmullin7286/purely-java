# purely-java

Note that this is an unreleased library and has not been deployed to Maven yet.

Pure functional programming concepts and collections translated to Java 21, with a focus on ease of use and
easy integration with existing standard Java APIs.

This library was heavily inspired by [vavr](https://github.com/vavr-io/vavr) and contains many of the same concepts
and patterns, with the goal of being more ergonomic using modern Java features, and with a focus on integrating
seamlessly with the existing Java standard library. As such, we've taken less opinionated approaches to APIs, such as
not implementing our own `Option` type and using the built-in `Optional` from the standard library.

Also note that this library is designed to be a functional companion and mirror to the Java standard library, and as
such new features such
as [Stream Gatherers](https://docs.oracle.com/en/java/javase/22/core/stream-gatherers-01.html#GUID-FE89C89E-38F4-49A0-8663-3EEC1BB9DAA0)
may require upgrades to the latest JDK. Bug fixes will be backported to older releases.

# Basic Concepts

## Immutability and Referential Transparency

Immutability and Referential Transparency are core concepts of Pure Functional programming that can lead to a much safer
and easier to reason about codebase.

### Immutability

Immutability essentially means that once a value is instantiated its state cannot be modified. Java has always had
partial support for immutability with the `final` keyword going back to the very beginning. However, for the majority
of the languages history immutability was not valued in the same way as functional languages like Haskell, and as such
common practices leaned towards mutable operations such as setters, for/while loops, and standard collections that
mutate on insertion or deletion.

Mutability has its place, but time has proven that immutability has some key advantages:

1. Automatic Thread Safety: As immutable values will never change after instantiation, they are automatically thread
   safe and eliminate the need for synchronization mechanisms.
2. Predictability: Mutable objects require you to keep track of two things in order to ensure correctness: The initial
   value, as well as the order of any side effect-ful operations that may have updated the state of the object over
   time. This can lead to many long days and nights over step through debugging that are eliminated if the object is
   guaranteed to never change or interact with any side effects, making the program easier to reason about.
3. Data Sharing: Since the object will never change, it can be safely shared among many other objects or parts of the
   program, eliminating the need for operations like defensive copying.

Although stateful and mutable operations and values may be necessary for extremely high performance scenarios where
every memory allocation matters, it's become apparent in modern language development that immutability by default is the
best practice for structuring and designing code, and has been embraced by modern language design, such as Java 17's
records being immutable objects, or Rust requiring an explicit `mut` keyword for any mutable variable.

### Referential Transparency

A method is considered referentially transparent if, for any call to the method, it may be replaced by its resulting
return value, and the behavior of the program would not change.

In other words, a method is referentially transparent if it satisfies the two conditions:

1. For any set of parameters, the method will always return the same output. Since Java is an Object-Oriented language,
   we can consider the enclosing class of a method to be an implicit parameter.
2. The method must not have any side effects. This includes mutating a variable outside the function scope, such as a
   setter, or reading data from outside the system, such as reading user input or a database.

The following method is referentially transparent:

```java
public record Vector2(int x, int y) {
    public Vector2 add(Vector2 other) {
        return new Vector2(x + other.x, y + other.y);
    }
}
```

The following methods are not referentially transparent:

```java
public class Vector2 {
    public int x;
    public int y;

    public void add(Vector2 other) {
        this.x = x + other.x;
        this.y = y + other.y;
    }

    public void addFromStdin() {
        final var scanner = new Scanner(System.in);
        System.out.print("Enter x: ");
        this.x = x + scanner.nextInt();
        System.out.print("Enter y: ");
        this.y = y + scanner.nextInt();
    }
}
```

Referential transparency is a powerful concept that gives us a few key benefits:

1. Easier Reasoning - Since we know that a referentially transparent method will not modify any state, we don't have to
   worry about how it will interact with different parts of the system, and we can prove its correctness purely from its
   contents
2. Modularity - Referentially transparent methods are easily composable to create new referentially transparent methods
3. Easy to test - No mocking is required because no side effects can happen. Referentially transparent methods are
   trivial to unit test.

### the @Pure annotation

This library annotation `org.purely.annotations.Pure`. When applied at the class level, this annotation denotes that
the class is immutable, and that all methods contained in the class are referentially transparent. When applied at the
method level, this annotation denotes that the specific method is referentially transparent, but the enclosing class
may be mutable.

At this time, there is no static analysis to determine that a class or method marked with @Pure is being honest, so
it is up to the developer to ensure that they follow the guidelines listed above.

For the most part, all classes exported by this package will be referentially transparent. Exceptions are made for
`Iterable`, `Collector`, or the various collection adapters which must be mutable to interface with the standard Java
API.

## Algebraic Data Types

In many functional and functional-inspired languages such as Haskell, OCaml, F#, Scala, and Rust, types are represented
as Algebraic Data Types. Algebraic Data Types are composites of other types of the following two forms:

### Product Types

A Product type is a type containing several fields. For example, in Haskell:

```haskell
data MyType = MyType Int Bool
```

is a product type of `Int * String`. This is called a product type because in effect the possible values of a product
type are the cartesian product of the set of all possible values of the field types.

Up through Java 16, Product Types were roughly equivalent to standard Plain Old Java Objects, or POJOs:

```java
public class MyType {
    private int myInt;
    private boolean myBool;

    public MyType(int myInt, boolean myBool) {
        this.myInt = myInt;
        this.myBool = myBool;
    }

    public void setMyBool(boolean myBool) {
        this.myBool = myBool;
    }

    public boolean getMyBool(boolean myBool) {
        return myBool;
    }

    public void setMyInt(int myInt) {
        this.myInt = myInt;
    }

    public int getMyInt() {
        return myInt;
    }
}
```

The traditional way of defining POJOS suffers from both mutability through use of setters and verbosity (the equivalent
one line of Haskell is 20 lines of Java). With Java 17, we now have records, which are effectively Java's version of
immutable, transparent Product types. The above code could be redefined as:

```java
public record MyType(int myInt, boolean myBool) {
}
```

One line of code can now create an immutable Product Type, just like Haskell.

### Sum Types

Sum types, known in some languages as variants, discriminated unions, or tagged unions, are types represent a value
that could be one of several different, but fixed, types. For example, in Haskell, we could define the type:

```haskell
data MySumType = MyString String | MyInt Int
```

The type above could either by a MyString containing a String, or a MyInt containing an Int, but not both. Essentially,
The possible values of a sum type is the sum of all possible values of its variants.

Historically in Java, we had no real way to represent this at the language level, so libraries such as
[vavr](https://github.com/vavr-io/vavr) were forced to use some clever workaround to simulate the same concept. However,
as of Java 17, we have the concept of sealed classes and interfaces, which are essentially Sum Types. The above can
now be written in Java as:

```java
sealed interface MySumType {
    record MyString(String value) implements MySumType {
    }

    record MyInt(int value) implements MyInt {
    }
} 
```

### Pattern Matching

One of the key benefits of Algebraic Data Types, and one that this library is heavily based on, is pattern matching,
which has been officially introduced to the Java language as of JDK 21. Pattern matching allows you to express what
operation to use for each case of a sum type, which allows for short and expressive code such as:

```java
sealed interface Tree {
    record Branch(int value, Tree left, Tree right) implements Tree {
    }

    record Leaf() implements Tree {
    }
}

static Tree insert(Tree t, int v) {
    return switch (t) {
        case Branch(int i, Tree left, Tree right) when i == v -> t;
        case Branch(int i, Tree left, Tree right) when i < v -> new Branch(i, left, insert(right, v));
        case Branch(int i, Tree left, Tree right) -> new Branch(i, insert(left), v);
        case Leaf() -> new Branch(v, new Leaf(), new Leaf());
    };
}
```

In one switch statement we've implemented an immutable binary tree insertion. This library leverages the power of
pattern matching to make types like `Either` and `Try` simple and easy to deal with, without having to use complex
nested .map() and .flatMap() operations.

# Library Features

Using the concepts provided by functional programming, this package provides the basic building blocks for pure,
immutable
code.

## The control Package

The control package contains useful classes that can be used for control flow in an application.

### Either

Either is a sum type that can represent one of two types of values, defined by the Left class and the Right class. This
can be useful for adhoc representations of data where you may expect two different types of values. In many functional
programming languages such as Haskell this type is used to define an operation that may error out. For example:

```java
enum ErrorReason {
    NOT_FOUND,
    NOT_OLD_ENOUGH
}

record Person(String name, int age) {
}

void doSomething() {
    switch (getVoter("Ryan")) {
        case Right(Person p) -> onSuccess(p);
        case Left(ErrorReason r) -> onFailure(r);
    }
}

Either<ErrorReason, Person> getVoter(String name) {
    getPersonByName(name)
            .map(p -> p.age() < 18 ? new Left<>(ErrorReason.NOT_OLD_ENOUGH) : new Right<>(p))
            .orElseGet(() -> new Left<>(NOT_FOUND));

}

Optional<Person> getPersonByName(String name) {
    // do some logic 
}
```

However, if your error case is a `Throwable`, it's recommended that you use `Try` instead.

The Either class also follows closely the Api of the `Optional` class and provides chaining methods to perform
operations over
either case as a shorthand for pattern matching. The api is not biased to either the left or right case like the
api for Scala's api, so each operation has a pair of left/right methods, such
as `mapLeft()`, `mapRight()`, `flatMapLeft()`, and `flatMapRight()`.

### Try

Checked exceptions are useful in that they require the caller of a method that throws such an exception to handle the
error case. However, checked exceptions are not without their pain points. One of these pain points of using functional 
programming tools like Streams in Java is that the built-in lambda types don't support checked exceptions.

If we think about representing our return values as Algebraic data types, the return value of any method that throws
an exception can be thought as a sum type of the return value as well as the exception type. Enter, `Try`.

Try is a control type that can be returned from a method to represent either a successful operation or an operation that
failed with an exception. Previously when using streams we would have to write something like:

```java
class Example {
    void example() {
       final List<T> myList = getList();
       try {
           myList.stream()
             .map(i -> {
                 try {
                     return operationThatThrows(i);
                 } catch (SomeException e) {
                     throw new RuntimeException(e);
                 }
             }).toList();
       } catch( RuntimeException e ) {
            if(e.getCause() instanceof SomeException s) {
                throw s;
            }
            throw e;
       }
    }
}
```

This is verbose and cumbersome, and you may want to just use traditional for loops instead. However, with `Try`, we
can now write:

```java
class Example {
    void example() {
        final List<T> myList = getList();
        final Try<List<T>> result = myList.stream()
            .map(Try.function(i -> operationThatThrows(i)))
            .collect(Try.collect(Collectors.toList()));
       
       switch (result) {
           case Success(List<T> l) -> doSomething(l);
           case Failure(Throwable t) -> onError(t);
       }
    }
}
```

`Try.function()` wraps a function that may throw an error, and creates a new standard `Function` that returns either
the result or the error thrown. `Try.collect` will run the collector passed in, but stop at the first failure encountered.
If any failure is encountered, that will be returned instead of the collector's result.