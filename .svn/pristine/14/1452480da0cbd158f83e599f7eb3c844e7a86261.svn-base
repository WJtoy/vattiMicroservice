package com.kkl.kklplus.b2b.vatti.utils;


import java.util.HashMap;
import java.util.Map;

public enum VattiStatusCodeEnum {



    PROGRESSING(4,"E0007","预约中"),
    APPOINTMENT(3, "E0008","预约"),
    CANCEL(10, "E0009","服务取消"),
    FINISH(5, "E0010","服务完成");

    public int kklCode;
    public String vattiCode;
    public String name;
    private static final Map<Integer, VattiStatusCodeEnum> MAP = new HashMap();

    private VattiStatusCodeEnum(int kklCode, String vattiCode,String name) {
        this.kklCode = kklCode;
        this.vattiCode = vattiCode;
        this.name = name;
    }

    public static VattiStatusCodeEnum get(int kklCode) {
        return valueOf(kklCode);
    }

    public static VattiStatusCodeEnum valueOf(Integer kklCode) {
        VattiStatusCodeEnum statusCode = null;
        if (kklCode != null) {
            statusCode = (VattiStatusCodeEnum)MAP.get(kklCode);
        }

        return statusCode;
    }

    static {
        MAP.put(PROGRESSING.kklCode,PROGRESSING);
        MAP.put(APPOINTMENT.kklCode, APPOINTMENT);
        MAP.put(CANCEL.kklCode, CANCEL);
        MAP.put(FINISH.kklCode, FINISH);
    }
}
