package io.github.ithamal.beanfetch.fetcher.adapt;

import io.github.ithamal.beanfetch.fetcher.Fetcher;

import java.util.*;

/**
 * 适配列表数据填充器
 * @author ken.lin
 * @since 1.0
 * @param <K> 键类型
 * @param <V> 值类型
 */
public abstract class ListFetcherAdapter<K, V> implements Fetcher<K, V> {

    /**
     * 填充
     * @param keys 键集合
     * @return 值集合
     */
    protected abstract List<V> fetch(List<K> keys);

    /**
     * 填充
     * @param keys 键集合
     * @return 键-值集合
     */
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
