package com.kkl.kklplus.b2b.vatti.http.config;

import okhttp3.OkHttpClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.concurrent.TimeUnit;

@EnableConfigurationProperties({B2BVattiProperties.class})
@Configuration
public class OKHttpConfig {

    @Bean
    public OkHttpClient okHttpClient(B2BVattiProperties tooneProperties) {
        return new OkHttpClient().newBuilder()
                .connectTimeout(tooneProperties.getOkhttp().getConnectTimeout(), TimeUnit.SECONDS)
                .writeTimeout(tooneProperties.getOkhttp().getWriteTimeout(), TimeUnit.SECONDS)
                .readTimeout(tooneProperties.getOkhttp().getReadTimeout(), TimeUnit.SECONDS)
                .pingInterval(tooneProperties.getOkhttp().getPingInterval(), TimeUnit.SECONDS)
                .retryOnConnectionFailure(tooneProperties.getOkhttp().getRetryOnConnectionFailure())
                .build();
    }

    @Bean
    public RetryTemplate simpleRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
        simpleRetryPolicy.setMaxAttempts(4);//4次，包含本次调用
        retryTemplate.setRetryPolicy(simpleRetryPolicy);
        //重试补偿
        ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
        exponentialBackOffPolicy.setInitialInterval(1000);
        exponentialBackOffPolicy.setMultiplier(2);
        exponentialBackOffPolicy.setMaxInterval(5000);
        retryTemplate.setBackOffPolicy(exponentialBackOffPolicy);
        return retryTemplate;
    }
}
