package io.github.ithamal.beanfetch.factory;


import io.github.ithamal.beanfetch.fetcher.Fetcher;

/**
 * @author: ken.lin
 * @date: 2023-09-20 10:23
 */
@SuppressWarnings("unchecked")
public class SimpleFetcherFactory implements FetcherFactory {

    private SimpleFetcherFactory(){

    }

    public final static SimpleFetcherFactory INSTANCE = new SimpleFetcherFactory();

    @Override
    public <T, K> Fetcher<T, K> getFetcher(Class<?> fetcherClass) {
        try {
            return (Fetcher<T, K>) fetcherClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
