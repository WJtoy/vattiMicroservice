package com.kkl.kklplus.b2b.vatti.http.utils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.kkl.kklplus.b2b.vatti.http.command.OperationCommand;
import com.kkl.kklplus.b2b.vatti.http.config.B2BVattiProperties;
import com.kkl.kklplus.b2b.vatti.http.response.RefreshTokenResponseData;
import com.kkl.kklplus.b2b.vatti.http.response.ResponseBody;
import com.kkl.kklplus.b2b.vatti.utils.SpringContextHolder;
import com.kkl.kklplus.b2b.vatti.utils.VattiUtils;
import com.kkl.kklplus.starter.redis.utils.RedisDefaultDbNewUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OkHttpUtils {

    private static final MediaType CONTENT_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    @Autowired
    private OkHttpClient okHttpClient ;

    @Autowired
    private B2BVattiProperties tooneProperties;

    private static Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

    private static Gson gsonDefault = new Gson();

    @Autowired
    private RedisDefaultDbNewUtils redisDefaultDbNewUtils;

    private static final String REDIS_VATTI_ACCESS_TOKEN = "vatti:access:token";
    //redis过期时间
    private static final Integer VATTI_TOKEN_EXPIRE = 6600;

    /**
     * 调用华帝接口(GET)
     * @param dataClass
     * @param <T>
     * @return
     */
    public <T> ResponseBody<T> getSyncGenericNew(HttpUrl url, Class<T> dataClass) {
        return getSyncGenericNew(url,dataClass,false);
    }

    /**
     * 调用华帝接口(GET)
     * @param url 地址
     * @param dataClass 返回类型
     * @param defaultFlag 默认格式JSON标识
     * @param <T>
     * @return
     */
    public <T> ResponseBody<T> getSyncGenericNew(HttpUrl url, Class<T> dataClass,boolean defaultFlag) {
        ResponseBody<T> responseBody = null;
        String accessToken = getAccessToken();
        if(StringUtils.isBlank(accessToken)){
            responseBody = new ResponseBody<>(ResponseBody.ErrorCode.TOKEN_ERROR);
            return  responseBody;
        }
        B2BVattiProperties.DataSourceConfig dataSourceConfig = tooneProperties.getDataSourceConfig();
        if (dataSourceConfig != null) {
            Request request = new Request.Builder()
                    .addHeader("access_token",accessToken)
                    .addHeader("appid",dataSourceConfig.getAppid())
                    .url(url)
                    .build();
            Call call = okHttpClient.newCall(request);
            try {
                Response response = call.execute();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        String responseBodyJson = response.body().string();
                        try {

                            responseBody = gson.fromJson(responseBodyJson, new TypeToken<ResponseBody>() {
                            }.getType());
                            responseBody.setOriginalJson(responseBodyJson);
                            if (responseBody.getErrorCode() == ResponseBody.ErrorCode.SUCCESS.code) {
                                try {
                                    T data;
                                    if(defaultFlag) {
                                        data = gsonDefault.fromJson(responseBodyJson, dataClass);
                                    }else{
                                        data = gson.fromJson(responseBodyJson, dataClass);
                                    }
                                    responseBody.setData(data);
                                } catch (Exception e) {
                                    return new ResponseBody<>(ResponseBody.ErrorCode.DATA_PARSE_FAILURE, e);
                                }
                            }
                        } catch (Exception e) {
                            responseBody = new ResponseBody<>(ResponseBody.ErrorCode.JSON_PARSE_FAILURE, e);
                            responseBody.setOriginalJson(responseBodyJson);
                            return responseBody;
                        }
                    } else {
                        responseBody = new ResponseBody<>(ResponseBody.ErrorCode.HTTP_RESPONSE_BODY_ERROR);
                    }
                } else {
                    responseBody = new ResponseBody<>(ResponseBody.ErrorCode.HTTP_STATUS_CODE_ERROR);
                }
            } catch (Exception e) {
                return new ResponseBody<>(ResponseBody.ErrorCode.REQUEST_INVOCATION_FAILURE, e);
            }
        } else {
            responseBody = new ResponseBody<>(ResponseBody.ErrorCode.REQUEST_PARAMETER_FORMAT_ERROR);
        }
        return responseBody;
    }
    /**
     * 调用华帝接口(POST)
     * @param command
     * @param dataClass
     * @param <T>
     * @return
     */
    public <T> ResponseBody<T> postSyncGenericNew(OperationCommand command, Class<T> dataClass) {
        String accessToken = getAccessToken();
        ResponseBody<T> responseBody = null;
        B2BVattiProperties.DataSourceConfig dataSourceConfig = tooneProperties.getDataSourceConfig();
        if (dataSourceConfig != null && command != null && command.getOpCode() != null &&
                command.getReqBody() != null && command.getReqBody().getClass().getName().equals(command.getOpCode().reqBodyClass.getName())) {
            String url = dataSourceConfig.getRequestMainUrl().concat("/").concat(command.getOpCode().apiUrl);
            String reqbodyJson = new Gson().toJson(command.getReqBody());
            RequestBody requestBody = RequestBody.create(CONTENT_TYPE_JSON, reqbodyJson);
            Request request = new Request.Builder()
                    .addHeader("appid",dataSourceConfig.getAppid())
                    .addHeader("access_token",accessToken)
                    .url(url)
                    .post(requestBody)
                    .build();
            Call call = okHttpClient.newCall(request);
            try {
                Response response = call.execute();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        String responseBodyJson = response.body().string();
                        try {
                            responseBody = gson.fromJson(responseBodyJson, new TypeToken<ResponseBody>() {
                            }.getType());
                            responseBody.setOriginalJson(responseBodyJson);
                            if (responseBody.getErrorCode() == ResponseBody.ErrorCode.SUCCESS.code) {
                                try {
                                    T data = gson.fromJson(responseBodyJson, dataClass);
                                    responseBody.setData(data);
                                } catch (Exception e) {
                                    return new ResponseBody<>(ResponseBody.ErrorCode.DATA_PARSE_FAILURE, e);
                                }
                            }
                        } catch (Exception e) {
                            responseBody = new ResponseBody<>(ResponseBody.ErrorCode.JSON_PARSE_FAILURE, e);
                            responseBody.setOriginalJson(responseBodyJson);
                            return responseBody;
                        }
                    } else {
                        responseBody = new ResponseBody<>(ResponseBody.ErrorCode.HTTP_RESPONSE_BODY_ERROR);
                    }
                } else {
                    responseBody = new ResponseBody<>(ResponseBody.ErrorCode.HTTP_STATUS_CODE_ERROR);
                }
            } catch (Exception e) {
                return new ResponseBody<>(ResponseBody.ErrorCode.REQUEST_INVOCATION_FAILURE, e);
            }
        } else {
            responseBody = new ResponseBody<>(ResponseBody.ErrorCode.REQUEST_PARAMETER_FORMAT_ERROR);
        }
        return responseBody;
    }

    @Autowired
    private RetryTemplate retryTemplate;

    /**
     * 从redis中获得token
     * 加入重试机制
     */
    public String getAccessToken() {
        String result = retryTemplate.execute(new RetryCallback<String, RuntimeException>() {
            // 重试操作
            @Override
            public String doWithRetry(RetryContext retryContext) throws RuntimeException {
                String accessToken = redisDefaultDbNewUtils.get(REDIS_VATTI_ACCESS_TOKEN,String.class);
                //timeout or not login
                if(accessToken == null || StringUtils.isBlank(accessToken)){
                    //lock
                    String requestId = com.kkl.kklplus.starter.redis.utils.StringUtils.uuid();
                    String lockKey = "Lock:"+REDIS_VATTI_ACCESS_TOKEN;
                    //获得锁
                    Boolean locked = redisDefaultDbNewUtils.getLock(lockKey, requestId, 60);
                    if(locked) {
                        try {
                            accessToken = getAccessTokenRetry();
                        }finally {
                            redisDefaultDbNewUtils.releaseLock(lockKey, requestId);
                        }
                    }else{
                        throw new RuntimeException("正在获取token.");
                    }
                }
                return accessToken;
            }
            //兜底回调
        }, new RecoveryCallback<String>() {
            @Override
            public String recover(RetryContext retryContext) throws RuntimeException {
                Throwable t = retryContext.getLastThrowable();
                if (t != null) {
                    log.error("after retry {} times, recovery method called!", retryContext.getRetryCount(), t);
                }
                return "";
            }
        });
        return result;
    }
    /**
     * 授权重试
     * @return
     */
    private String getAccessTokenRetry(){
        B2BVattiProperties.DataSourceConfig dataSourceConfig = tooneProperties.getDataSourceConfig();
        String url = dataSourceConfig.getRequestMainUrl().concat("/").concat(VattiUtils.GET_TOKEN_URL);
        Request.Builder request = new Request.Builder()
                .addHeader("appid",dataSourceConfig.getAppid())
                .addHeader("appsecret",dataSourceConfig.getAppsecret())
                .url(url);
        RefreshTokenResponseData responseData = syncGenericNewCall(request.build());
        if(responseData != null && responseData.getCode() == VattiUtils.SUCCESS_CODE){
            String accessToken = responseData.getData().getAccessToken();
            try{
                redisDefaultDbNewUtils.setEX(REDIS_VATTI_ACCESS_TOKEN, accessToken, VATTI_TOKEN_EXPIRE);
                return accessToken;
            }catch (Exception e){
                log.error("redis setEX:{}",REDIS_VATTI_ACCESS_TOKEN,e);
            }
        }
        return "";
    }

    public RefreshTokenResponseData syncGenericNewCall(Request request) {
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBodyJson = response.body().string();
                RefreshTokenResponseData responseBody = gson.fromJson(responseBodyJson,RefreshTokenResponseData.class);
                return responseBody;
            }
            return null;
        } catch (Exception e) {
            log.error("syncGenericNewCall", e);
            return null;
        }
    }
}
