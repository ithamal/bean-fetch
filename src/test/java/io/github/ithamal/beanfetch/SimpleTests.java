package io.github.ithamal.beanfetch;

import com.fasterxml.jackson.databind.json.JsonMapper;
import io.github.ithamal.beanfetch.fetcher.FetchType;
import io.github.ithamal.beanfetch.fetcher.BeanFetcher;
import io.github.ithamal.beanfetch.convert.StrToIntListSplitter;
import io.github.ithamal.beanfetch.util.CollectionUtil;
import io.github.ithamal.beanfetch.util.SingleMap;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author: ken.lin
 * @since: 2023-09-19 17:28
 */
@Component
public class SimpleTests {

    public static class RoleRepository {
        public Map<Integer, Role> findMapByIds(Collection<Integer> keys) {
            System.out.println("加载键：" + keys);
            List<Role> roleList = Arrays.asList(new Role(1, "角色1"), new Role(2, "角色2"));
            return CollectionUtil.toMap(roleList, Role::getId);
        }
    }

    @Test
    public void test() throws Exception {
        List<UserPo> poList = Arrays.asList(
                new UserPo("张三", "1,2"),
                new UserPo("李四", "1")
        );
        RoleRepository roleRepository = new RoleRepository();
        BeanFetcher<User, UserPo> beanFetcher = new BeanFetcher<User, UserPo>()
//                .many(FetchType.EAGER, User::setRoles, UserPo::getRoleIds, StrToIntListSplitter.INSTANCE, RoleFilter.class)
//                .many(FetchType.LAZY, User::setRoles, UserPo::getRoleIds, StrToIntListSplitter.INSTANCE, roleRepository::findMapByIds)
                .many(FetchType.LAZY, User::setRoles, UserPo::getRoleIds, StrToIntListSplitter.INSTANCE, new RoleListFilter())
//                .single(FetchType.EAGER, User::setRole, UserPo::getRoleIds, StrToIntListSplitter.INSTANCE, RoleFilter.class)
                .single(FetchType.LAZY, User::setRole, UserPo::getRoleIds, StrToIntListSplitter.INSTANCE, (k) -> {
                    System.out.println("加载键：" + k);
                    return new SingleMap<>(1, new Role(1, "特殊"));
                })
                ;
        List<User> userList = beanFetcher.toBean(poList, it -> {
            return new User(it.getName());
        });
        System.out.println("-------------- 热加载结束 ---------------------");
        System.out.println(new JsonMapper().writerWithDefaultPrettyPrinter().writeValueAsString(userList));
//        for (User user : userList) {
//            printUser(user);
//        }
    }

    private static void printUser(User user) {
        System.out.println("User{" +
                "name='" + user.getName() + '\'' +
                ", role=" + user.getRole() +
                ", roles=" + user.getRoles() +
                '}');
    }
}
