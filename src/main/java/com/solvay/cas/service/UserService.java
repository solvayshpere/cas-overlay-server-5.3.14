package com.solvay.cas.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

public class UserService {

    public Map<String, Object> findByUserName(String username) {
        Map<String, Object> user = new HashMap<>();
        if("admin".equalsIgnoreCase(username)){
            user.put("uid", "4");
            user.put("username", "admin");
            user.put("password", "123456");
            user.put("name", "admin");
            user.put("state", "0");
        }else if("test".equalsIgnoreCase(username)){
            user.put("uid", "5");
            user.put("username", "test");
            user.put("password", "123456");
            user.put("name", "test");
            user.put("state", "0");
        }
        return user;
    }

    public void userLogin(String username, String password, String appCode) {
    }
}
