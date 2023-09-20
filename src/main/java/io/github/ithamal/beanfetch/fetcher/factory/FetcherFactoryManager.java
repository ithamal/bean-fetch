package io.github.ithamal.beanfetch.fetcher.factory;


import io.github.ithamal.beanfetch.support.SpringContextAware;

/**
 * @author: ken.lin
 * @date: 2023-09-20 10:23
 */
public class FetcherFactoryManager  {

    public static FetcherFactory getFactory(){
        if(SpringContextAware.CONTEXT == null){
            return SimpleFetcherFactory.INSTANCE;
        }else{
            return SpringFetcherFactory.INSTANCE;
        }
    }
}
