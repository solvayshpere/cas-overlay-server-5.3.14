package com.solvay.cas.config;

import com.solvay.cas.listener.TGTCreateEventListener;
import com.solvay.cas.service.TriggerLogoutService;
import com.solvay.cas.service.UserIdObtainServiceImpl;
import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 登出配置
 *
 * @author solvay
 * @date 2020/8/29
 */
@Configuration("casLogoutTriggerConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class CasLogoutTriggerConfiguration {
    @Autowired
    private CentralAuthenticationService centralAuthenticationService;

    /**
     * 触发登出服务
     *
     * @return 触发登出服务
     */
    @Bean
    protected TriggerLogoutService triggerLogoutService() {
        return new TriggerLogoutService(centralAuthenticationService);
    }

    @Bean
    //注册事件监听tgt的创建
    protected TGTCreateEventListener tgtCreateEventListener() {
        TGTCreateEventListener listener = new TGTCreateEventListener(triggerLogoutService(), new UserIdObtainServiceImpl());
        return listener;
    }
}