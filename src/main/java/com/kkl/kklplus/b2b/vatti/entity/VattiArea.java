package com.kkl.kklplus.b2b.vatti.entity;

import lombok.Data;

@Data
public class VattiArea {

    private Long id;

    private Long parentId;

    private String parentIds;

    private String code;

    private String name;

    private String fullName;

    private Integer type;

    private Long createAt;

}
