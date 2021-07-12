package com.kkl.kklplus.b2b.vatti.controller;

import com.google.gson.Gson;
import com.kkl.kklplus.b2b.vatti.service.B2BProcesslogService;
import com.kkl.kklplus.b2b.vatti.service.VattiOrderInfoService;
import com.kkl.kklplus.common.exception.MSErrorCode;
import com.kkl.kklplus.common.response.MSResponse;
import com.kkl.kklplus.entity.b2b.common.B2BProcessFlag;
import com.kkl.kklplus.entity.b2bcenter.rpt.B2BOrderProcesslog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/vatti")
public class OrderPushController {

    @Autowired
    private B2BProcesslogService b2BProcesslogService;

    @Autowired
    private VattiOrderInfoService orderInfoService;



    @GetMapping("/order")
    public MSResponse updateOrder(HttpServletRequest req) {
        Map<String, String[]> parameterMap = req.getParameterMap();
        //String json = getRequestJson(req);
        B2BOrderProcesslog processlog = b2BProcesslogService.savelog
                ("order", new Gson().toJson(parameterMap));
        MSResponse msResponse = orderInfoService.process(req.getParameter("guid"),req.getParameter("object_id"));
        if(msResponse.getCode() != MSErrorCode.CODE_VALUE_SUCCESS){
            processlog.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_FAILURE.value);
            processlog.setProcessComment(msResponse.getMsg());
        } else {
            processlog.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_SUCESS.value);
        }
        processlog.preUpdate();
        b2BProcesslogService.updateProcessFlag(processlog);
        return msResponse;
    }



    private String getRequestJson(HttpServletRequest req) throws IOException {
        // 读取参数
        InputStream inputStream;
        StringBuffer sb = new StringBuffer();
        inputStream = req.getInputStream();
        String s;
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        while ((s = in.readLine()) != null) {
            sb.append(s);
        }
        in.close();
        inputStream.close();
        String json = sb.toString();
        return json;
    }
}
