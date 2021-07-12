package com.kkl.kklplus.b2b.vatti.http.response;

import lombok.Data;

@Data
public class RefreshTokenResponseData extends ResponseData{

    private Integer code = 0;

    private String reason = "";

    private RefreshToken data;

    @Data
    public static class RefreshToken {

        private String accessToken;

        private String refreshToken;

        private String expiredAt;

    }
}
