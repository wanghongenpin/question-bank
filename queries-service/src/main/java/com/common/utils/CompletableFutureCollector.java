package com.common.utils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author wanghongen
 * 2018/10/16
 */
public final class CompletableFutureCollector {

    public static final CompletableFuture[] EMPTY_FUTURES = new CompletableFuture[0];

    private CompletableFutureCollector() {
    }

    public static <X, T extends CompletableFuture<X>> Collector<T, ?, CompletableFuture<List<X>>> collectResult() {
        return Collectors.collectingAndThen(Collectors.toList(), joinResult());
    }

    public static <T extends CompletableFuture<?>> Collector<T, ?, CompletableFuture<Void>> allComplete() {
        return Collectors.collectingAndThen(Collectors.toList(), CompletableFutureCollector::allOf);
    }

    private static <X, T extends CompletableFuture<X>> Function<List<T>, CompletableFuture<List<X>>> joinResult() {
        return ls -> allOf(ls)
                .thenApply(v -> ls
                        .stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()));
    }

    private static <T extends CompletableFuture<?>> CompletableFuture<Void> allOf(List<T> ls) {
        return CompletableFuture.allOf(ls.toArray(EMPTY_FUTURES));
    }
}
