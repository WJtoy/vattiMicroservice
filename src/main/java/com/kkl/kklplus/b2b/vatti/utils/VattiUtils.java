package com.kkl.kklplus.b2b.vatti.utils;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

public class VattiUtils {

    public final static int SUCCESS_CODE = 0;

    public final static String TODOLIST_PARAM = "?partnerId=101347";

    public final static String GETSERVICEORDER = "getServiceOrder";

    public final static String INSTALL = "ZIC3";

    public final static String REPAIR = "ZIC4";

    public final static String INWARRANTY = "保内";

    public final static String CHECKPROCESSFLAG = "/vattiOrderInfo/checkWorkcardProcessFlag";

    public final static String UPDATETRANSFERRESULT = "/vattiOrderInfo/updateTransferResult";

    public final static String ORDERLIST = "/vattiOrderInfo/getList";

    public final static String REQUESTMETHOD = "POST";

    public final static String GET_TOKEN_URL = "api/access_token";
    /**
     * 返回时间状态码 01 12点前 02 18点前 03 21点前后
     * @param time
     * @return
     */
    public static String returnDateCode(Long time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int i = calendar.get(Calendar.HOUR_OF_DAY);
        if( i < 12 ){
            return "01";
        }else if( i < 18 ){
            return "02";
        }else if (i < 21){
            return "03";
        }else{
            String strDateFormat = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
            return sdf.format(time);
        }
    }

    private static final String DEFAULT_URL_ENCODING = "UTF-8";
    /**
     * MD5
     * @param str
     * @return
     */
    public  static  String getMD5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] c = md.digest(str.getBytes(DEFAULT_URL_ENCODING));
            String md5str = Base64.getEncoder().encodeToString(c);
            return md5str;
        }catch (Exception e){
            return null;
        }
    }
}
