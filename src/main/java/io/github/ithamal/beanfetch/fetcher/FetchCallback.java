package io.github.ithamal.beanfetch.fetcher;

import io.github.ithamal.beanfetch.util.SingleList;
import io.github.ithamal.beanfetch.util.SingleMap;
import lombok.*;

/**
 * @author: ken.lin
 * @since: 2023-09-20 15:15
 */
@Builder
@AllArgsConstructor
public class FetchCallback<T, S> {

    private final T target;

    private final S source;

    private final FetchMeta meta;

    private final BeanFetcher<T, S> fetcher;

    private boolean fetched;

    public Object fetch() {
        if (!fetched) {
            SingleMap<S, T> beanMap = new SingleMap<>(source, target);
            SingleList<S> sourceList = new SingleList<>(source);
            fetcher.fetch(beanMap, sourceList, meta);
            fetched = true;
        }
        return target;
    }
}