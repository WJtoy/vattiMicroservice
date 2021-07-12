package com.kkl.kklplus.b2b.vatti.http.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "vatti")
public class B2BVattiProperties {

    @Getter
    @Setter
    private String appid;

    @Getter
    @Setter
    private String appsecret;

    @Getter
    @Setter
    private String[] methods;

    @Getter
    private final OkHttpProperties okhttp = new OkHttpProperties();


    public static class OkHttpProperties {
        /**
         * 设置连接超时
         */
        @Getter
        @Setter
        private Integer connectTimeout = 10;

        /**
         * 设置读超时
         */
        @Getter
        @Setter
        private Integer writeTimeout = 10;

        /**
         * 设置写超时
         */
        @Getter
        @Setter
        private Integer readTimeout = 10;

        /**
         * 是否自动重连
         */
        @Getter
        @Setter
        private Boolean retryOnConnectionFailure = true;

        /**
         * 设置ping检测网络连通性的间隔
         */
        @Getter
        @Setter
        private Integer pingInterval = 0;
    }

    /**
     * 数据源配置
     */
    @Getter
    private final DataSourceConfig dataSourceConfig = new DataSourceConfig();

    public static class DataSourceConfig {
        @Getter
        @Setter
        private String requestMainUrl;

        @Getter
        @Setter
        private String appid;

        @Getter
        @Setter
        private String appsecret;

        @Getter
        @Setter
        private String companyId;

        @Getter
        @Setter
        private String ivDealer2;

        @Getter
        @Setter
        private String userId;

        @Getter
        @Setter
        private Boolean scheduleEnabled = false;

        @Getter
        @Setter
        private Boolean orderMqEnabled = false;
    }
}
