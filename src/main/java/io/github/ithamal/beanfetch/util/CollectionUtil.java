package io.github.ithamal.beanfetch.util;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author: ken.lin
 * @since: 2023-09-19 17:55
 */
public class CollectionUtil {

    public static <K,V> Map<K,V> toMap(Collection<V> collection, Function<V, K> mapper){
        return collection.stream().collect(Collectors.toMap(mapper, Function.identity()));
    }
}
