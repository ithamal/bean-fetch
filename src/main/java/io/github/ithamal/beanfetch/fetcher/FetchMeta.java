package io.github.ithamal.beanfetch.fetcher;

import io.github.ithamal.beanfetch.convert.KeyConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Function;

/**
 * @author: ken.lin
 * @date: 2023-09-20 13:32
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
public class FetchMeta {

    public final boolean isMany;

    public final ValueSetter setter;

    public final Function keyMapper;

    public final KeyConverter keyConverter;

    public final Fetcher fetcher;

    private final FetchType type ;
}
