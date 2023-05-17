package com.xiya.accounts.comm;

import com.xiya.accounts.service.AccountService;
import com.xiya.accounts.service.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    private AccountService accountService;

    public InterceptorConfig(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(
                new JwtAuthenticationFilter(accountService))
                .addPathPatterns("/**")
                .excludePathPatterns("/**/account/checkLogin/**");
    }
}
