package com.solvay.cas.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

public class RoleService {
    public Set<String> findAllRoles() {
        Set<String> roles = new HashSet<>();
        roles.add("admin");
        roles.add("test");
        return roles;
    }

    public String findRolesByUserId(String uid) {
        if(uid.equalsIgnoreCase("4")){
            return "admin";
        }
        return "test";
    }
}
