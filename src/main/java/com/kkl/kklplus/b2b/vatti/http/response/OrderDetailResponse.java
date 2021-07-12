package com.kkl.kklplus.b2b.vatti.http.response;

import lombok.Data;

import java.util.List;
@Data
public class OrderDetailResponse {
    private List<OrderDetail> data;
}
