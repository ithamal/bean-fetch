package io.github.ithamal.beanfetch.fetcher;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author: ken.lin
 * @since: 2023-09-20 09:39
 */
@FunctionalInterface
public interface Fetcher<K, V> {

    Map<K,V> fetch(Collection<K> keys);
}
