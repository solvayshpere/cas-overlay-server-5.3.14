package com.solvay.cas.adaptors.generic;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apereo.cas.authentication.RememberMeUsernamePasswordCredential;

import javax.validation.constraints.Size;

/**
 * @author: solvay
 * @date: 2020/8/31
 * @description: 验证码 Credential   RememberMeUsernamePasswordCredential
 */
public class RememberMeUsernamePasswordCaptchaCredential extends RememberMeUsernamePasswordCredential {

    @Size(min = 5,max = 5, message = "require captcha")
    private String captcha;

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(this.captcha)
                .toHashCode();
    }
}