package com.kkl.kklplus.b2b.vatti.http.response;

import lombok.Data;

/**
 * describe:
 *
 * @author chenxj
 * @date 2020/09/01
 */
@Data
public class OrderResponse {
    /**
     * CRM 订单对象的 GUID
     */
    private String guid;
    /**
     * 业务伙伴编号
     */
    private String ivDealer;
    /**
     *
     */
    private String iIvDealer2;
    /**
     * 处理标识
     */
    private String objectId;
    /**
     * 业务类型
     */
    private String processType;
    /**
     * 业务的过账日期
     */
    private String postingDate;
    private String stat;
    /**
     * 用户状态
     */
    private String concatstatuser;
    /**
     * 状态参数文件
     */
    private String stsma;
    /**
     *
     */
    private String createdAt;
    private String createdTim;
    /**
     * 更改时间（用户时区中的输出）
     */
    private String changedAt;
    /**
     * 工单修改日期
     */
    private String changedDat;
    /**
     * 工单修改时间
     */
    private String changedTim;
    private String zzfld00001k;//信息来源
    private String zzfld00001o;//购买商店
    private String zzdld00003j;//预约日期
    private String zzdld00001v;//预约上门时间
    private String zzdld000054;//工程师
    private String zzdld00001r;//条形码数据
    private String zzdld00002y;//购买日期
    private String zzdld00003h;//维修性质
    private String zzdld00003i;//服务方式
    private String zzdld000057;//产品大类
    private String zzdld000059;//产品类别
    private String zzdld00005A;//故障现象
    private String zzdld00005B;//故障原因
    private String zzdld00001Y;//回访结果
    private String zzdld00001Z;//原因名称
    private String zzdld00004J;//原因描述
    private String zzdld000025;//服务态度的满意情况
    private String zzdld00001M;//购买渠道
    private String zzdld00006Y;//服务省份
    private String zzdld00006Z;//服务城市
    /**
     * 上次更改交易者
     */
    private String changedBy;
    //名称 1
    private String name;
    //
    private String regiontxt;
    //城市
    private String city;
    //区域
    private String district;
    //街道
    private String street;
    //第一个电话号码：区号 + 号码
    private String tel1Numbr;
    private String tel2Numbr;
    private String zwillOverLei;
    private String zwillOverDat;
    private String zwillOverTim;
    //用于删除时的唯一码= 工单的guid
    private String keyGuid;
}
