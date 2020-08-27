package com.solvay.cas.domain;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = -52465023298303421L;

    private Integer uid;
    /**
     * 用户名
     */
    private String username;
    /**
     * 登录密码
     */
    private String password;
    /**
     * 用户真实姓名
     */
    private String name;
    /**
     * 用户身份证号
     */
    private String idCardNum;
    /**
     * 用户状态：0:正常状态,1：用户被锁定
     */
    private String state;


    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdCardNum() {
        return idCardNum;
    }

    public void setIdCardNum(String idCardNum) {
        this.idCardNum = idCardNum;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
