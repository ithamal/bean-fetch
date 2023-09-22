package io.github.ithamal.beanfetch.fetcher.enhance;

import io.github.ithamal.beanfetch.fetcher.BeanFetcher;
import io.github.ithamal.beanfetch.fetcher.FetchMeta;
import io.github.ithamal.beanfetch.fetcher.FetchType;
import io.github.ithamal.beanfetch.fetcher.ValueSetter;
import io.github.ithamal.beanfetch.util.SingleList;
import lombok.SneakyThrows;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: ken.lin
 * @since: 2023-09-20 13:32
 */
public class BeanEnhancer {

    public static <T, S> T enhance(BeanFetcher<T,S> beanFetcher, T target, S source ) {
        Class<?> beanClass = target.getClass();
        return (T) Enhancer.create(beanClass,new BeanProxyInterceptor<>(beanFetcher, target, source));
    }
}
