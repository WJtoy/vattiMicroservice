package com.kkl.kklplus.b2b.vatti.entity;


import com.kkl.kklplus.entity.b2b.common.B2BBase;
import lombok.Data;

@Data
public class VattiUpdateOrder extends B2BBase<VattiUpdateOrder>{

    private String guid;
    /**
     * 预约上门日期 格式：yyyyMMdd
     */
    private Long appointmentDate;
    /**
     * 预约上门时间01 12点前 02 18点前 03 21点前
     */
    private String appointmentTime;
    /**
     * 购买渠道 01线上渠道 02线下渠道
     */
    private String buyType;
    /**
     * 改约原因 01 用户改约 02 缺少配件 03天气原因 04 其他原因
     */
    private String toReasonAbout;
    /**
     * 是否改约 01 是 02否 03多次改约
     */
    private String isAppointment;
    /**
     * 取消原因 01用户取消 03号码错误 04联系不上 05派错工单 06 其他原因
     */
    private String canelReason;
    /**
     * 网点跟进内容TDID=ZICE
     */
    private String followUpContent;
    /**
     * 条形码
     */
    private String barCode;
    /**
     * 序号
     */
    private String numberInt;
    /**
     * 产品编码
     */
    private String orderedProd;
    /**
     * 产品描述
     */
    private String description;
    /**
     * 数量
     */
    private String quantity;
    /**
     * 网点处理结果
     */
    private String disposeResult;
    /**
     * 维修性质 01: 保修期内 02: 保修期外 03: 保外单据保内维修
     */
    private String warrantyType;
    /**
     * 维修措施 01已维修 02调试03 安装性返修 04 已退货 05 已换货 06其它
     */
    private String serviceMeasures;
    /**
     * 服务方式 01 上门服务 02 在点维修
     */
    private String serviceMode;
    /**
     * 服务完成日期 格式：yyyyMMdd
     */
    private Long serviceFinishDate;
    /**
     * 服务完成时间 01 12点前 02 18点前 03 21点前
     */
    private String serviceFinishTime;
    /**
     * 上传图片
     */
    private String urlPath;
    /**
     * 故障现象
     */
    private String failurPhen;
    /**
     * 故障原因
     */
    private String failurReason;
    /**
     * 服务省份
     */
    private String serviceProvince;
    /**
     * 服务城市
     */
    private String serviceCity;
    /**
     * 购买日期 格式：yyyyMMdd
     */
    private String buyerDate;
    /**
     * 工单状态代码
     */
    private Integer status;
    /**
     * 工单用户状态
     */
    private String userStatProc;
    /**
     * 客户地址的省份
     */
    private String region;
    /**
     * 城市编号
     */
    private String cityNo;
    /**
     * 城市名称(以这个中文为准)
     */
    private String city;
    /**
     * 产品大类
     */
    private String productCategory;
    /**
     * 工单开始工作S，工作结束工作E
     */
    private String startAndEndWork;
    /**
     * 信息来源
     */
    private String informationSources;
    /**
     * 工程师驳回原因
     */
    private String engineerReason;


}


