package io.github.ithamal.beanfetch.fetcher;


import java.io.Serializable;

/**
 * @author: ken.lin
 * @since: 2023-09-20 10:51
 */
@FunctionalInterface
public interface ValueSetter<T, V> extends Serializable {

    void set(T obj, V value);
}
