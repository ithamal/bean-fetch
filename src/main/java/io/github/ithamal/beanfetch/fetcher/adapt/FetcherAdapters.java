package io.github.ithamal.beanfetch.fetcher.adapt;

import io.github.ithamal.beanfetch.fetcher.Fetcher;

import java.util.*;
import java.util.function.Function;

/**
 * 适配器集
 *
 * @author ken.lin
 * @since 1.0
 */
public abstract class FetcherAdapters {

    public static <K, V> ListFetcherAdapter<K, V> list(Function<Collection<K>, List<V>> fetcher, Function<V, K> keyMapper) {
        return new ListFetcherAdapter<K, V>() {
            @Override
            public K extraKey(V value) {
                return keyMapper.apply(value);
            }

            @Override
            protected List<V> fetch(List<K> keys) {
                return fetcher.apply(keys);
            }
        };
    }

    public static <K, V> SingleFetcherAdapter<K, V> single(Function<K, V> function) {
        return new SingleFetcherAdapter<K, V>() {
            @Override
            protected V fetchOne(K key) {
                return function.apply(key);
            }
        };
    }
}
