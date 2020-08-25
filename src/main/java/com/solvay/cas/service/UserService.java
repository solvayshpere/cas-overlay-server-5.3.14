package com.solvay.cas.service;

import java.util.HashMap;
import java.util.Map;

public class UserService {

    public Map<String, Object> findByUserName(String username) {
        Map<String, Object> user = new HashMap<>();
        if("admin".equalsIgnoreCase(username)){
            user.put("uid", "4");
            user.put("username", "admin");
            user.put("password", "e10adc3949ba59abbe56e057f20f883e");
            user.put("name", "admin");
            user.put("state", "0");
        }else if("test".equalsIgnoreCase(username)){
            user.put("uid", "5");
            user.put("username", "test");
            user.put("password", "ed0290f05224a188160858124a5f5077");
            user.put("name", "test");
            user.put("state", "0");
        }
        return user;
    }
}
