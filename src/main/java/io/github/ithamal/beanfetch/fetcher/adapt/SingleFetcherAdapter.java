package io.github.ithamal.beanfetch.fetcher.adapt;

import io.github.ithamal.beanfetch.fetcher.Fetcher;
import io.github.ithamal.beanfetch.util.SingleMap;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author: ken.lin
 * @since: 2023-09-20 17:15
 */
public abstract class SingleFetcherAdapter<K, V> implements Fetcher<K, V> {

    protected abstract V fetchOne(K key);

    @Override
    public Map<K, V> fetch(Collection<K> keys) {
        if (keys.isEmpty()) {
            return Collections.emptyMap();
        }
        K key = keys.iterator().next();
        return new SingleMap<>(key, fetchOne(key));
    }
}
