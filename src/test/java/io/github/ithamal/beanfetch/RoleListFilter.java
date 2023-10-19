package io.github.ithamal.beanfetch;

import io.github.ithamal.beanfetch.fetcher.Fetcher;
import io.github.ithamal.beanfetch.fetcher.adapt.ListFetcherAdapter;
import lombok.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author: ken.lin
 * @since: 2023-10-19 17:08
 */
public class RoleListFilter extends ListFetcherAdapter<Integer, Role> {

    @Override
    public Integer extraKey(Role value) {
        return value.getId();
    }

    @Override
    protected List<Role> fetch(List<Integer> keys) {
        return Arrays.asList(new Role(1, "角色1"), new Role(2, "角色2"));
    }
}
