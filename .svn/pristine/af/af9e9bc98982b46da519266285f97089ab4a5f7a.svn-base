package com.kkl.kklplus.b2b.vatti.config;

import com.kkl.kklplus.b2b.vatti.handler.VattiSecurityHandler;
import com.kkl.kklplus.b2b.vatti.http.config.B2BVattiProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 *
 * @author: Jeff.Zhao
 * @date: 2018/8/20 10:08
 */
@Configuration
public class ApiConfig extends WebMvcConfigurerAdapter {
    @Autowired
    VattiSecurityHandler vattiSecurityHandler;
    @Autowired
    B2BVattiProperties vattiProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(vattiSecurityHandler).addPathPatterns(vattiProperties.getMethods());
        super.addInterceptors(registry);
    }
}
