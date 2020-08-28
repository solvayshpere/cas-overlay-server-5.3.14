package com.solvay.cas.exception;

import javax.security.auth.login.AccountException;

/**
 * @author: solvay
 * @date: 2020/8/31
 * @description: 用户没找到异常
 */
public class MyAccountNotFoundException extends AccountException {
    public MyAccountNotFoundException() {
        super();
    }

    public MyAccountNotFoundException(String msg) {
        super(msg);
    }
}