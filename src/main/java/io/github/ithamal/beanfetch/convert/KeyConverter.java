package io.github.ithamal.beanfetch.convert;

/**
 * <pre>键类型转换器</pre>
 * @author ken.lin
 * @since 1.0
 * @param <S> 源类型
 * @param <T> 目标类型
 */
public interface KeyConverter<S, T> {

    /**
     * 转换
     * @param source 源值
     * @return 转换结果
     */
    T[] convert(S source);
}
