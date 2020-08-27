package com.solvay.cas.adaptors.generic;

import com.solvay.cas.CustomPasswordEncoder;
import com.solvay.cas.domain.User;
import com.solvay.cas.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.exceptions.AccountDisabledException;
import org.apereo.cas.authentication.exceptions.AccountPasswordMustChangeException;
import org.apereo.cas.authentication.handler.support.AbstractPreAndPostProcessingAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.ServicesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.security.auth.login.AccountException;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import javax.servlet.http.HttpServletRequest;
import java.security.GeneralSecurityException;
import java.util.Map;

/**
 * @author: solvay
 * @date: 2020/8/31
 * @description:
 */
public class RememberMeUsernamePasswordCaptchaAuthenticationHandler extends AbstractPreAndPostProcessingAuthenticationHandler {
    private static final Logger logger = LoggerFactory.getLogger(RememberMeUsernamePasswordCaptchaAuthenticationHandler.class);

    public RememberMeUsernamePasswordCaptchaAuthenticationHandler(String name, ServicesManager servicesManager, PrincipalFactory principalFactory, Integer order) {
        super(name, servicesManager, principalFactory, order);
    }

    @Override
    protected AuthenticationHandlerExecutionResult doAuthentication(Credential credential) throws GeneralSecurityException, PreventedException {
        RememberMeUsernamePasswordCaptchaCredential myCredential = (RememberMeUsernamePasswordCaptchaCredential) credential;
        String requestCaptcha = myCredential.getCaptcha();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 校验验证码
        Object attribute = request.getSession().getAttribute("captcha");
        String realCaptcha = attribute == null ? null : attribute.toString();
        if(StringUtils.isBlank(requestCaptcha) || !requestCaptcha.toUpperCase().equals(realCaptcha)){
            throw new FailedLoginException("验证码错误");
        }

        // 获取请求来源URL
        String referer = request.getHeader("referer");
        if(referer.indexOf("service=")>0){
            referer = referer.substring(referer.indexOf("service=")+8);
            referer.replace("%3A",":");
            referer.replace("%2F","/");
        }

        RegisteredService service = findByServiceId(referer);
        if (service != null){
            throw new FailedLoginException("未查询到Service错误");
        }
        //String appCode = service.getName();
        //userService.userLogin(myCredential.getUsername(),myCredential.getPassword(),appCode);

        String username = myCredential.getUsername();
        // JDBC模板依赖于连接池来获得数据的连接，所以必须先要构造连接池
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://134.134.2.70:33061/cas?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&serverTimezone=GMT%2B8");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");

        // 创建JDBC模板
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);

        String sql = "SELECT * FROM user_info WHERE username = ?";

        User user = (User) jdbcTemplate.queryForObject(sql, new Object[]{username}, new BeanPropertyRowMapper(User.class));

        System.out.println("database username : "+ user.getUsername());
        System.out.println("database password : "+ user.getPassword());

        if (user == null) {
            throw new AccountNotFoundException("用户名输入错误或用户名不存在");
        }

        // 密码加密验证(MD5 32位 大写)
        CustomPasswordEncoder passwordEncoder = new CustomPasswordEncoder();

        //可以在这里直接对用户名校验,或者调用 CredentialsMatcher 校验
        if (!passwordEncoder.matches(myCredential.getPassword(), user.getPassword())) {
            throw new FailedLoginException("用户名或密码错误！");
        }

        //这里将 密码对比 注销掉,否则 无法锁定  要将密码对比 交给 密码比较器 在这里可以添加自己的密码比较器等
        //if (!password.equals(user.getPassword())) {
        //    throw new IncorrectCredentialsException("用户名或密码错误！");
        //}
        if ("1".equals(user.getState())) {
            throw new FailedLoginException("账号已被锁定,请联系管理员！");
        }
        return createHandlerResult(credential, this.principalFactory.createPrincipal(username));
    }

    @Override
    public boolean supports(Credential credential) {
        return credential instanceof RememberMeUsernamePasswordCaptchaCredential;
    }

    public RegisteredService findByServiceId(String serviceId){
        RegisteredService service = null;
        try {
            service = servicesManager.findServiceBy(serviceId);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return service;
    }
}