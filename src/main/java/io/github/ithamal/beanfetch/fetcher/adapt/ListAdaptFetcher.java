package io.github.ithamal.beanfetch.fetcher.adapt;

import io.github.ithamal.beanfetch.fetcher.Fetcher;

import java.util.*;

/**
 * @author: ken.lin
 * @date: 2023-09-20 17:15
 */
public abstract class ListAdaptFetcher<K, V> implements Fetcher<K, V> {

    protected abstract List<V> fetch(List<K> keys);

    @Override
    public Map<K, V> fetch(Collection<K> keys) {
        if (keys.isEmpty()) {
            return Collections.emptyMap();
        }
        List<K> keyList;
        if (keys instanceof List) {
            keyList = (List<K>) keys;
        } else {
            keyList = new ArrayList<>(keys.size());
            keyList.addAll(keys);
        }
        List<V> valueList = fetch(keyList);
        Map<K, V> hashMap = new HashMap<>();
        for (int i = 0; i < keyList.size(); i++) {
            K key = keyList.get(i);
            V value = valueList.get(i);
            hashMap.put(key, value);
        }
        return hashMap;
    }
}
