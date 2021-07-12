package com.kkl.kklplus.b2b.vatti.http.response;

import com.kkl.kklplus.b2b.vatti.entity.VattiOrderInfo;
import lombok.Data;

import java.util.List;

@Data
public class OrderInfoResponseData extends ResponseData{

    private List<VattiOrderInfo> data;
}
