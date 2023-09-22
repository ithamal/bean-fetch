package io.github.ithamal.beanfetch.fetcher.enhance;

import io.github.ithamal.beanfetch.fetcher.BeanFetcher;
import io.github.ithamal.beanfetch.fetcher.FetchMeta;
import io.github.ithamal.beanfetch.util.SingleList;
import io.github.ithamal.beanfetch.util.SingleMap;
import lombok.*;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * @author: ken.lin
 * @since: 2023-09-22 19:07
 */
public class BeanProxyInterceptor<T, S> implements MethodInterceptor {

    private final BeanFetcher<T, S> beanFetcher;

    private final T target;

    private final S source;

    public BeanProxyInterceptor(BeanFetcher<T, S> beanFetcher, T target, S source) {
        this.beanFetcher = beanFetcher;
        this.target = target;
        this.source = source;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        FetchMeta meta = beanFetcher.findLazyMethodMeta(method.getName());
        if (meta != null) {
            Object value = meta.getGetter().invoke(target, args);
            if (value == null) {
                SingleMap<S, T> beanMap = new SingleMap<S, T>(source, target);
                SingleList<S> sourceList = new SingleList<>(source);
                beanFetcher.fetch(beanMap, sourceList, meta);
            } else {
                return value;
            }
        }
        return method.invoke(target, args);
    }
}
