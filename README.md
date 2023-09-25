### maven依赖
```xml
<dependency>
    <groupId>io.github.ithamal</groupId>
    <artifactId>beanfetch</artifactId>
    <version>1.0.3</version>
</dependency>
```

### 示例代码
```java
  public static class RoleRepository {
    public Map<Integer, Role> findMapByIds(Collection<Integer> keys) {
        System.out.println("加载键：" + keys);
        List<Role> roleList = Arrays.asList(new Role(1, "角色1"), new Role(2, "角色2"));
        return CollectionUtil.toMap(roleList, Role::getId);
    }
}

    public static void main(String[] args) throws Exception {
        List<UserPo> poList = Arrays.asList(
                new UserPo("张三", "1,2"),
                new UserPo("李四", "1")
        );
        BeanFetcher<User, UserPo> beanFetcher = new BeanFetcher<User, UserPo>()
                .many(FetchType.EAGER, User::setRoles, UserPo::getRoleIds, StrToIntListSplitter.INSTANCE, RoleFilter.class)
                .many(FetchType.EAGER, User::setRoles, UserPo::getRoleIds, StrToIntListSplitter.INSTANCE, roleRepository::findMapByIds)
                .single(FetchType.EAGER, User::setRole, UserPo::getRoleIds, StrToIntListSplitter.INSTANCE, RoleFilter.class)
                .single(FetchType.LAZY, User::setRole, UserPo::getRoleIds, StrToIntListSplitter.INSTANCE, (k) -> {
                    System.out.println("加载键：" + k);
                    return new SingleMap<>(1, new Role(1, "特殊"));
                });
        List<User> userList = beanFetcher.toBean(poList, it -> {
            return new User(it.getName());
        });
        System.out.println("-------------- 热加载结束 ---------------------");
        for (User user : userList) {
            System.out.println(user);
        }
    }
}

```