package com.kkl.kklplus.b2b.vatti.entity;

import com.kkl.kklplus.entity.b2b.common.B2BBase;
import lombok.Data;

@Data
public class VattiOrderInfo extends B2BBase<VattiOrderInfo>{

    private Long kklOrderId;
    private String kklOrderNo;
    private String guid;
    private String appointmentDate;
    private String appointmentTime;
    private String buyType;
    private String toReasonAbout;
    private String isAppointment;
    private String canelReason;
    private String followUpContent;
    private String barCode;
    private String numberInt;
    private String quantity;
    private String disposeResult;
    private String warrantyType;
    private String serviceMeasures;
    private String serviceMode;
    private String serviceFinishDate;
    private String serviceFinishTime;
    private String urlPath;
    private String failurPhen;
    private String failurReason;
    private String serviceProvince;
    private String serviceCity;
    private String buyerDate;
    private String userStatProc;
    private String regionNo;
    private String region;
    private String cityNo;
    private String city;
    private Integer processFlag;
    private Integer processTime;
    private String processComment;
    private Long createBy;
    private Long createDt;
    private Long updateBy;
    private Long updateDt;
    private String quarter;
    private String ivDealer;
    private String ivDealer2;
    private String objectId;
    private String processType;
    private String postingDate;
    private String stat;
    private String concatstatuser;
    private String stsma;
    private String changedAt;
    private String changedDat;
    private String changedTim;
    private String district;
    private String districtNo;
    private String street;
    private String keyGuid;
    private String productCategory;
    private String productCategory2;
    private String zwillOverLei;
    private String zwillOverDat;
    private String zwillOverTim;
    private String buyShop;
    private String returnResult;
    private String engineer;
    private String changedBy;
    private String reasonName;
    private String reasonDescription;
    private String informationSources;
    private String tel1Number;
    private String tel2Number;
    private String name;

}
