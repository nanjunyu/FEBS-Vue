package com.invs.oss.common.function;

@FunctionalInterface
public interface CacheSelector<T> {
    T select() throws Exception;
}
