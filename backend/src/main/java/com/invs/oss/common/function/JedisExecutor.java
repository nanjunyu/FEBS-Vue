package com.invs.oss.common.function;

import com.invs.oss.common.exception.RedisConnectException;

@FunctionalInterface
public interface JedisExecutor<T, R> {
    R excute(T t) throws RedisConnectException;
}
