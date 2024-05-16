package qqserver.service;

import qq.common.User;

import java.util.HashMap;

//存储用户，模拟数据库
public class CurrentUserDB {
    public static HashMap<String, User> validUsers = new HashMap<>();
    static{ //静态代码块中初始化hm
        validUsers.put("aaa",new User("aaa","123"));
        validUsers.put("至尊宝",new User("至尊宝","123"));
        validUsers.put("灰太羊",new User("灰太羊","123"));
        validUsers.put("紫霞仙子",new User("紫霞仙子","123"));
    }

}
