package io.github.ithamal.beanfetch.convert;


import org.springframework.util.StringUtils;

/**
 * @author: ken.lin
 * @since: 2023-09-20 10:35
 */
public class StrToIntListSplitter implements KeyConverter<String, Integer> {

    private StrToIntListSplitter() {

    }

    public final static StrToIntListSplitter INSTANCE = new StrToIntListSplitter();

    @Override
    public Integer[] convert(String source) {
        if (!StringUtils.hasText(source)) {
            return new Integer[0];
        }
        String[] split = source.trim().split(",");
        Integer[] array = new Integer[split.length];
        for (int i = 0; i < split.length; i++) {
            array[i] = Integer.parseInt(split[i]);
        }
        return array;
    }
}
