package io.github.ithamal.beanfetch.fetcher;

import io.github.ithamal.beanfetch.convert.KeyConverter;
import io.github.ithamal.beanfetch.fetcher.enhance.BeanEnhancer;
import io.github.ithamal.beanfetch.fetcher.factory.FetcherFactory;
import io.github.ithamal.beanfetch.fetcher.factory.FetcherFactoryManager;
import io.github.ithamal.beanfetch.util.SingleList;

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

    private FetcherFactory fetcherFactory = FetcherFactoryManager.getFactory();

    public void setFetcherFactory(FetcherFactory fetcherFactory) {
        this.fetcherFactory = fetcherFactory;
    }

    public <K, SK, V> BeanFetcher<T, S> many(FetchType type, ValueSetter<T, List<V>> setter, Function<S, K> keyMapper,
                                             KeyConverter<K, SK> keyConverter, Class<? extends Fetcher<SK, V>> fetcherClass) {
        Fetcher<?, ?> fetcher = fetcherFactory.getFetcher(fetcherClass);
        metaList.add(FetchMeta.builder()
                .isMany(true)
                .type(type)
                .setter(setter)
                .keyMapper(keyMapper)
                .keyConverter(keyConverter)
                .fetcher(fetcher)
                .build());
        return this;
    }

    public <K, SK, V> BeanFetcher<T, S> many(FetchType type, ValueSetter<T, List<V>> setter, Function<S, K> keyMapper,
                                             KeyConverter<K, SK> keyConverter, Fetcher<SK, V> fetcher) {
        metaList.add(FetchMeta.builder()
                .isMany(true)
                .type(type)
                .setter(setter)
                .keyMapper(keyMapper)
                .keyConverter(keyConverter)
                .fetcher(fetcher)
                .build());
        return this;
    }

    public <K, SK, V> BeanFetcher<T, S> single(FetchType type, ValueSetter<T, V> setter, Function<S, K> keyMapper,
                                               KeyConverter<K, SK> keyConverter, Class<? extends Fetcher<SK, V>> fetcherClass) {
        Fetcher<?, ?> fetcher = fetcherFactory.getFetcher(fetcherClass);
        metaList.add(FetchMeta.builder()
                .isMany(false)
                .type(type)
                .setter(setter)
                .keyMapper(keyMapper)
                .keyConverter(keyConverter)
                .fetcher(fetcher)
                .build());
        return this;
    }

    public <K, SK, V> BeanFetcher<T, S> single(FetchType type, ValueSetter<T, V> setter, Function<S, K> keyMapper,
                                               KeyConverter<K, SK> keyConverter, Fetcher<SK, V> fetcher) {
        metaList.add(FetchMeta.builder()
                .isMany(false)
                .type(type)
                .setter(setter)
                .keyMapper(keyMapper)
                .keyConverter(keyConverter)
                .fetcher(fetcher)
                .build());
        return this;
    }

    public void fill(List<S> list) {
        toBean(list, t -> (T) t);
    }

    public T toBean(S source, Function<S, T> mapper) {
        SingleList<S> sourceList = new SingleList<>(source);
        List<T> resultList = toBean(sourceList, mapper);
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    public List<T> toBean(List<S> sourceList, Function<S, T> mapper) {
        HashMap<S, T> beanMap = new HashMap<>();
        List<T> beanList = new ArrayList<>(sourceList.size());
        for (S source : sourceList) {
            T bean = mapper.apply(source);
            beanList.add(bean);
            beanMap.put(source, bean);
        }
        for (FetchMeta meta : metaList) {
            if (meta.getType() == FetchType.EAGER) {
                fetch(beanMap, sourceList, meta);
            } else {
                for (S source : sourceList) {
                    T target = beanMap.get(source);
                    fillProxy(source, target, meta);
                }
            }
        }
        return beanList;
    }

    public void fetch(Map<S, T> beanMap, List<S> sourceList, FetchMeta meta) {
        Collection<Object> keys = collectKeys(sourceList, meta, true);
        Map<Object, T> resultMap = meta.fetcher.fetch(keys);
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

    private void fillProxy(S source, T target, FetchMeta meta) {
        FetchCallback<T, S> callback = FetchCallback.<T, S>builder().fetcher(this).source(source).target(target).meta(meta).build();
        if (meta.isMany) {
            Object value = BeanEnhancer.enhanceMany(meta.setter, callback);
            meta.setter.set(target, value);
        } else {
            Object value = BeanEnhancer.enhanceSingle(meta.setter, callback);
            meta.setter.set(target, value);
        }
    }

    private Collection<Object> collectKeys(List<S> sourceList, FetchMeta meta, boolean distinct) {
        Collection<Object> keys = distinct ? new HashSet<>() : new ArrayList<>();
        for (S source : sourceList) {
            Object key = meta.keyMapper.apply(source);
            if (meta.keyConverter != null) {
                keys.addAll(Arrays.asList(meta.keyConverter.convert((String) key)));
            } else {
                keys.add(key);
            }
        }
        return keys;
    }


}
