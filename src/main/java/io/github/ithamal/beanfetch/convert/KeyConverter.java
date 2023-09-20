package io.github.ithamal.beanfetch.convert;

/**
 * @author: ken.lin
 * @date: 2023-09-20 10:35
 */
public interface KeyConverter<S, T> {

    T[] convert(S source);
}
