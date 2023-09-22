package io.github.ithamal.beanfetch;


import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author: ken.lin
 * @since: 2023-09-19 17:44
 */
@Data
@AllArgsConstructor
public class UserPo {

    private String name;

    private String roleIds;
}
