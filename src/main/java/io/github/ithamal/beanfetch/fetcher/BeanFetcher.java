package io.github.ithamal.beanfetch.fetcher;

import io.github.ithamal.beanfetch.convert.KeyConverter;
import io.github.ithamal.beanfetch.fetcher.enhance.BeanEnhancer;
import io.github.ithamal.beanfetch.fetcher.factory.FetcherFactory;
import io.github.ithamal.beanfetch.fetcher.factory.FetcherFactoryManager;
import io.github.ithamal.beanfetch.util.SingleList;
import lombok.SneakyThrows;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author: ken.lin
 * @since: 2023-09-20 09:38
 */
@SuppressWarnings("unchecked")
public class BeanFetcher<T, S> {

    private final List<FetchMeta> metaList = new ArrayList<>();

    private final Map<String, FetchMeta> lazyMethodMap = new HashMap<>();

    private FetcherFactory fetcherFactory = FetcherFactoryManager.getFactory();

    private boolean hasLazy;

    public void setFetcherFactory(FetcherFactory fetcherFactory) {
        this.fetcherFactory = fetcherFactory;
    }

    public <K, SK, V> BeanFetcher<T, S> many(FetchType type, ValueSetter<T, List<V>> setter, Function<S, K> keyMapper,
                                             KeyConverter<K, SK> keyConverter, Class<? extends Fetcher<SK, V>> fetcherClass) {
        hasLazy = hasLazy || type == FetchType.LAZY;
        Fetcher<?, ?> fetcher = fetcherFactory.getFetcher(fetcherClass);
        addMeta(true, type, setter, keyMapper, keyConverter, fetcher);
        return this;
    }

    public <K, SK, V> BeanFetcher<T, S> many(FetchType type, ValueSetter<T, List<V>> setter, Function<S, K> keyMapper,
                                             KeyConverter<K, SK> keyConverter, Fetcher<SK, V> fetcher) {
        addMeta(true, type, setter, keyMapper, keyConverter, fetcher);
        return this;
    }

    public <K, SK, V> BeanFetcher<T, S> single(FetchType type, ValueSetter<T, V> setter, Function<S, K> keyMapper,
                                               KeyConverter<K, SK> keyConverter, Class<? extends Fetcher<SK, V>> fetcherClass) {
        hasLazy = hasLazy || type == FetchType.LAZY;
        Fetcher<?, ?> fetcher = fetcherFactory.getFetcher(fetcherClass);
        addMeta(false, type, setter, keyMapper, keyConverter, fetcher);
        return this;
    }

    public <K, SK, V> BeanFetcher<T, S> single(FetchType type, ValueSetter<T, V> setter, Function<S, K> keyMapper,
                                               KeyConverter<K, SK> keyConverter, Fetcher<SK, V> fetcher) {
        addMeta(false, type, setter, keyMapper, keyConverter, fetcher);
        return this;
    }

    private void addMeta(boolean isMany, FetchType type, ValueSetter setter, Function keyMapper, KeyConverter keyConverter,
                         Fetcher fetcher) {
        hasLazy = hasLazy || type == FetchType.LAZY;
        FetchMeta meta = FetchMeta.builder()
                .isMany(isMany)
                .type(type)
                .setter(setter)
                .getter(getGetter(setter))
                .keyMapper(keyMapper)
                .keyConverter(keyConverter)
                .fetcher(fetcher)
                .build();
        if (meta.getType() == FetchType.LAZY) {
            lazyMethodMap.put(meta.getGetter().getName(), meta);
        }
        metaList.add(meta);
    }

    public FetchMeta findLazyMethodMeta(String methodName) {
        return lazyMethodMap.get(methodName);
    }

    public void fill(List<S> list) {
        toBean(list, t -> (T) t);
    }

    public T toBean(S source, Function<S, T> mapper) {
        if (source == null) return null;
        SingleList<S> sourceList = new SingleList<>(source);
        List<T> resultList = toBean(sourceList, mapper);
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    public List<T> toBean(List<S> sourceList, Function<S, T> mapper) {
        HashMap<S, T> beanMap = new HashMap<>();
        List<T> beanList = new ArrayList<>(sourceList.size());
        for (S source : sourceList) {
            T bean = mapper.apply(source);
            if (hasLazy) {
                bean = (T) BeanEnhancer.enhance(this, bean, source);
            }
            beanList.add(bean);
            beanMap.put(source, bean);
        }
        for (FetchMeta meta : metaList) {
            if (meta.getType() == FetchType.EAGER) {
                fetch(beanMap, sourceList, meta);
            } else {

            }
        }
        return beanList;
    }

    public void fetch(Map<S, T> beanMap, List<S> sourceList, FetchMeta meta) {
        Collection<Object> keys = collectKeys(sourceList, meta, true);
        Map<Object, T> resultMap = keys.isEmpty() ? Collections.emptyMap() : meta.fetcher.fetch(keys);
        SingleList<S> tmpList = new SingleList<>();
        for (S source : sourceList) {
            tmpList.add(source);
            Collection<Object> subKeys = collectKeys(tmpList, meta, false);
            List<Object> subList = subKeys.stream().map(resultMap::get).collect(Collectors.toList());
            T bean = beanMap.get(source);
            if (meta.isMany) {
                meta.setter.set(bean, subList);
            } else if (!subList.isEmpty()) {
                meta.setter.set(bean, subList.get(0));
            }
        }
    }

    private Collection<Object> collectKeys(List<S> sourceList, FetchMeta meta, boolean distinct) {
        Collection<Object> keys = distinct ? new HashSet<>() : new ArrayList<>();
        for (S source : sourceList) {
            Object key = meta.keyMapper.apply(source);
            if (key == null) {
                continue;
            }
            if (meta.keyConverter != null) {
                keys.addAll(Arrays.asList(meta.keyConverter.convert((String) key)));
            } else {
                keys.add(key);
            }
        }
        return keys;
    }

    public List<FetchMeta> getMetaList() {
        return metaList;
    }

    @SneakyThrows
    private Method getGetter(ValueSetter<?, ?> setter) {
        Method method = setter.getClass().getDeclaredMethod("writeReplace");
        method.setAccessible(true);
        SerializedLambda lambda = (SerializedLambda) method.invoke(setter);
        String implClassName = lambda.getImplClass().replace("/", ".");
        String getMethodName = lambda.getImplMethodName().replace("set", "get");
        return Class.forName(implClassName).getDeclaredMethod(getMethodName);
    }
}
