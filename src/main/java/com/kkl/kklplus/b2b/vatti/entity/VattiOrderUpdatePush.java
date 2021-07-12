package com.kkl.kklplus.b2b.vatti.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class VattiOrderUpdatePush implements Serializable{

    private String guid;
    private String objectId;
    private String onlineNo;
    private String attr1;
    private String attr2;
    private String attr3;
}
