package io.github.ithamal.beanfetch;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author: ken.lin
 * @date: 2023-09-19 17:44
 */
@Data
@NoArgsConstructor
public class User {

    private String name;

    private Role role;

    private List<Role> roles;

    public User(String name){
        this.name = name;
    }
}
