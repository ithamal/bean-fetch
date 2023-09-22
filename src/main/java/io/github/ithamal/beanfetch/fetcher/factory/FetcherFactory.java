package io.github.ithamal.beanfetch.fetcher.factory;

import io.github.ithamal.beanfetch.fetcher.Fetcher;

/**
 * @author: ken.lin
 * @since: 2023-09-20 10:00
 */
public interface FetcherFactory {

    <T,K> Fetcher<T, K> getFetcher(Class<?> fetcherClass);
}
