package io.github.ithamal.beanfetch.fetcher.factory;


import io.github.ithamal.beanfetch.fetcher.Fetcher;
import io.github.ithamal.beanfetch.support.SpringContextAware;

/**
 * @author: ken.lin
 * @since: 2023-09-20 10:23
 */
@SuppressWarnings("unchecked")
public class SpringFetcherFactory implements FetcherFactory {

    private SpringFetcherFactory(){

    }

    public final static SpringFetcherFactory INSTANCE = new SpringFetcherFactory();

    @Override
    public <T, K> Fetcher<T, K> getFetcher(Class<?> fetcherClass) {
        return (Fetcher<T, K>) SpringContextAware.CONTEXT.getBean(fetcherClass);
    }
}
