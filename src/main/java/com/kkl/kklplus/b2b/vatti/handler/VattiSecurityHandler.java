package com.kkl.kklplus.b2b.vatti.handler;

import com.kkl.kklplus.b2b.vatti.http.config.B2BVattiProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 飞羽安全验证
 * @author: Jeff.Zhao
 * @date: 2018/8/20 9:59
 */
@Slf4j
@Configuration
public class VattiSecurityHandler extends HandlerInterceptorAdapter {
    @Autowired
    private B2BVattiProperties vattiProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String appKey = "";
        String appSecret = "";
        try {
            appKey = request.getHeader("appid") == null ? "" : request.getHeader("appid");
            appSecret = request.getHeader("appsecret") == null ? "" : request.getHeader("appsecret");
        }catch (Exception e){
            log.error("权限验证", e);
        }
        if (appKey.equals(vattiProperties.getAppid()) &&
            appSecret.equals(vattiProperties.getAppsecret())){
            return true;
        }
        throw new Exception("非法请求,身份验证失败.");
    }
}
