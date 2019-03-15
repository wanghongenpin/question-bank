package com.common.utils;


import com.common.function.FallibleSupplier;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author wanghongen
 * 2018/5/24
 */
public abstract class Try<T> {
    private Try() {
    }

    public abstract boolean isSuccess();

    public abstract boolean isFailure();

    public abstract T get();

    public abstract void forEach(Consumer<? super T> action);


    public abstract <U> Try<U> map(Function<? super T, ? extends U> mapper);


    public abstract <U> Try<U> flatMap(Function<? super T, ? extends Try<U>> mapper);

    public abstract Try<T> filter(Predicate<? super T> predicate);


    public abstract Try<T> recover(Function<? super Throwable, ? extends T> recoverFunc);

    public abstract Try<T> recoverWith(Function<? super Throwable, ? extends Try<T>> recoverFunc);

    public abstract Try<Throwable> failed();

    public abstract Optional<T> toOptional();


    public T getOrElse(T defaultValue) {
        return isSuccess() ? get() : defaultValue;
    }

    public abstract Try<T> orElse(Try<T> defaultValue);

    public abstract <U> U transform(Function<? super T, ? extends U> successFunc,
                                    Function<Throwable, ? extends U> failureFunc);


    public static <T extends AutoCloseable, R> Function<T, Try<R>> apply(Function<T, R> consumer) {
        return closeable -> Try.apply(() -> {
            try (T in = closeable) {
                return consumer.apply(in);
            }
        });
    }


    public static <T> Try<T> apply(FallibleSupplier<T> supplier) {
        try {
            return new Success<>(supplier.get());
        } catch (Throwable e) {
            return new Failure<>(e);
        }
    }

    public static <T> Try<T> success(T value) {
        Objects.requireNonNull(value);
        return new Success<>(value);
    }

    public static <T> Try<T> failure(Throwable ex) {
        Objects.requireNonNull(ex);
        return new Failure<>(ex);
    }

    @SuppressWarnings("unchecked")
    public static <T> Try<T> join(Try<Try<T>> t) {
        if (t == null) return new Failure<>(new NullPointerException("T是空的"));
        else if (t instanceof Failure<?>) return (Try<T>) t;
        else return t.get();
    }


    public static final class Success<T> extends Try<T> {
        private final T value;

        public Success(T value) {
            this.value = value;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public boolean isFailure() {
            return false;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public void forEach(Consumer<? super T> action) {
            action.accept(value);
        }

        @Override
        public <U> Try<U> map(Function<? super T, ? extends U> mapper) {
            return Try.apply(() -> mapper.apply(value));
        }

        @Override
        public <U> Try<U> flatMap(Function<? super T, ? extends Try<U>> mapper) {
            return Try.join(Try.apply(() -> mapper.apply(value)));
        }

        @Override
        public Try<T> filter(Predicate<? super T> predicate) {
            return predicate.test(value) ? this : Try.failure(new NoSuchElementException("谓词不成立" + value));

        }

        @Override
        public Try<T> recover(Function<? super Throwable, ? extends T> recoverFunc) {
            return this;
        }

        @Override
        public Try<T> recoverWith(Function<? super Throwable, ? extends Try<T>> recoverFunc) {
            return this;
        }

        @Override
        public Try<Throwable> failed() {
            return new Failure<>(new UnsupportedOperationException("Success.failed"));
        }

        @Override
        public Optional<T> toOptional() {
            return Optional.ofNullable(value);
        }

        @Override
        public Try<T> orElse(Try<T> defaultValue) {
            return this;
        }

        @Override
        public <U> U transform(Function<? super T, ? extends U> successFunc,
                               Function<Throwable, ? extends U> failureFunc) {
            return successFunc.apply(value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Success success = (Success) o;

            return value.equals(success.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public String toString() {
            return "Success{" +
                    "value=" + value +
                    '}';
        }
    }

    public static final class Failure<T> extends Try<T> {
        private final RuntimeException runtimeThrowable;
        private final Throwable Throwable;

        public Failure(Throwable Throwable) {
            this.runtimeThrowable = new RuntimeException(Throwable);
            this.Throwable = Throwable;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public boolean isFailure() {
            return true;
        }

        @Override
        public T get() {
            throw runtimeThrowable;
        }


        @Override
        public void forEach(Consumer<? super T> action) {
        }

        @Override
        public <U> Try<U> map(Function<? super T, ? extends U> mapper) {
            return Try.failure(Throwable);
        }

        @Override
        public <U> Try<U> flatMap(Function<? super T, ? extends Try<U>> mapper) {
            return Try.failure(Throwable);
        }

        @Override
        public Try<T> filter(Predicate<? super T> predicate) {
            return this;
        }

        @Override
        public Try<T> recover(Function<? super Throwable, ? extends T> recoverFunc) {
            return Try.apply(() -> recoverFunc.apply(Throwable));
        }

        @Override
        public Try<T> recoverWith(Function<? super Throwable, ? extends Try<T>> recoverFunc) {
            return Try.join(Try.apply(() -> recoverFunc.apply(Throwable)));
        }

        @Override
        public Try<Throwable> failed() {
            return new Success<>(Throwable);
        }

        @Override
        public Optional<T> toOptional() {
            return Optional.empty();
        }


        @Override
        public Try<T> orElse(Try<T> defaultValue) {
            return defaultValue;
        }

        @Override
        public <U> U transform(Function<? super T, ? extends U> successFunc,
                               Function<Throwable, ? extends U> failureFunc) {
            return failureFunc.apply(Throwable);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Failure failure = (Failure) o;

            return failure.Throwable.equals(Throwable);

        }

        @Override
        public int hashCode() {
            return Throwable.hashCode();
        }

        @Override
        public String toString() {
            return "Failure{" +
                    "Throwable=" + Throwable +
                    '}';
        }
    }
}
