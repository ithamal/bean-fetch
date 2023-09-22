package io.github.ithamal.beanfetch;

import io.github.ithamal.beanfetch.fetcher.Fetcher;
import io.github.ithamal.beanfetch.util.CollectionUtil;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author: ken.lin
 * @since: 2023-09-19 17:52
 */
@Component
public class RoleFilter implements Fetcher<Integer, Role> {

    @Override
    public Map<Integer, Role> fetch(Collection<Integer> keys) {
        System.out.println("加载键：" + keys);
        List<Role> roleList = Arrays.asList(new Role(1, "角色1"), new Role(2, "角色2"));
        return CollectionUtil.toMap(roleList, Role::getId);
    }
}
