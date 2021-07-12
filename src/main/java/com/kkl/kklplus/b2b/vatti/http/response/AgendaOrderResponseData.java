package com.kkl.kklplus.b2b.vatti.http.response;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class AgendaOrderResponseData extends ResponseData{

    private List<AgendaOrder> data;

    @Data
    public static class AgendaOrder{

        private String guid;

        private String ivDealer;

        private String ivDealer2;

        private String objectId;

        private String processType;

        private String postingDate;

        private String stat;

        private String concatstatuser;

        private String stsma;

        private String createdAt;

        private String createdTim;

        private String changedAt;

        private String changedDat;

        private String changedTim;

        private String zzfld00001k;

        private String zzfld00001o;

        private String zzfld00003j;

        private String zzfld00001v;

        private String changedBy;

        private String zzfld000054;

        private String zzfld00001r;

        private String zzfld00002y;

        private String zzfld00003h;

        private String zzfld00003i;

        private String zzfld000057;

        private String zzfld000059;

        private String zzfld00005a;

        private String zzfld00005b;

        private String zzfld00001y;

        private String zzfld00001z;

        private String zzfld00004j;

        private String zzfld000025;

        private String zzfld00001m;

        private String zzfld00006y;

        private String zzfld00006z;

        private String name;

        private String regiontxt;

        private String city;

        private String district;

        private String street;

        private String tel1Numbr;

        private String tel2Numbr;

        private String zwillOverLei;

        private String zwillOverDat;

        private String zwillOverTim;

        private String keyGuid;
    }
}
