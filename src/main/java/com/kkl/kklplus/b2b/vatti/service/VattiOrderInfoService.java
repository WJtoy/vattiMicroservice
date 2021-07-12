package com.kkl.kklplus.b2b.vatti.service;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kkl.kklplus.b2b.vatti.entity.SysLog;
import com.kkl.kklplus.b2b.vatti.entity.VattiArea;
import com.kkl.kklplus.b2b.vatti.entity.VattiOrderInfo;
import com.kkl.kklplus.b2b.vatti.entity.VattiOrderUpdatePush;
import com.kkl.kklplus.b2b.vatti.http.command.OperationCommand;
import com.kkl.kklplus.b2b.vatti.http.config.B2BVattiProperties;
import com.kkl.kklplus.b2b.vatti.http.response.*;
import com.kkl.kklplus.b2b.vatti.http.utils.OkHttpUtils;
import com.kkl.kklplus.b2b.vatti.mapper.B2BProcesslogMapper;
import com.kkl.kklplus.b2b.vatti.mapper.SysLogMapper;
import com.kkl.kklplus.b2b.vatti.mapper.VattiOrderInfoMapper;
import com.kkl.kklplus.b2b.vatti.mq.sender.B2BOrderMQSender;
import com.kkl.kklplus.b2b.vatti.utils.QuarterUtils;
import com.kkl.kklplus.b2b.vatti.utils.VattiUtils;
import com.kkl.kklplus.common.exception.MSErrorCode;
import com.kkl.kklplus.common.response.MSResponse;
import com.kkl.kklplus.entity.b2b.common.B2BProcessFlag;
import com.kkl.kklplus.entity.b2b.order.B2BWorkcardQtyDaily;
import com.kkl.kklplus.entity.b2b.pb.MQB2BWorkcardQtyDailyMessage;
import com.kkl.kklplus.entity.b2bcenter.md.B2BDataSourceEnum;
import com.kkl.kklplus.entity.b2bcenter.md.B2BShopEnum;
import com.kkl.kklplus.entity.b2bcenter.pb.MQB2BOrderMessage;
import com.kkl.kklplus.entity.b2bcenter.rpt.B2BOrderProcesslog;
import com.kkl.kklplus.entity.b2bcenter.sd.B2BOrder;
import com.kkl.kklplus.entity.b2bcenter.sd.B2BOrderSearchModel;
import com.kkl.kklplus.entity.b2bcenter.sd.B2BOrderTransferResult;
import com.kkl.kklplus.entity.common.MSPage;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class VattiOrderInfoService {

    @Autowired
    private B2BOrderMQSender b2BOrderMQSender;

    @Autowired
    private VattiAreaService areaService;

    @Resource
    private VattiOrderInfoMapper vattiOrderInfoMapper;

    @Resource
    private SysLogMapper sysLogMapper;

    @Resource
    private B2BProcesslogMapper b2BProcesslogMapper;

    @Autowired
    private B2BVattiProperties vattiProperties;

    @Autowired
    private OkHttpUtils okHttpUtils;

    @Autowired
    private B2BProcesslogService b2BProcesslogService;

    /**
     * 添加华帝的工单信息
     * @param agendaOrders
     */
    public void addOrderInfo(List<AgendaOrderResponseData.AgendaOrder> agendaOrders) {

        for(AgendaOrderResponseData.AgendaOrder agendaOrder : agendaOrders){
            String guid = agendaOrder.getGuid();
            Long id = vattiOrderInfoMapper.findOrderByGuid(guid);
            // 判断是否存在该工单
            if(id == null){
                B2BOrderProcesslog b2BProcesslog = new B2BOrderProcesslog();
                b2BProcesslog.preInsert();
                b2BProcesslog.setInterfaceName(VattiUtils.GETSERVICEORDER);
                b2BProcesslog.setInfoJson("{\"guid\":\""+guid+"\"}");
                b2BProcesslog.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_ACCEPT.value);
                b2BProcesslog.setProcessTime(0);
                b2BProcesslog.setCreateById(1L);
                b2BProcesslog.setUpdateById(1L);
                b2BProcesslog.setQuarter(QuarterUtils.getQuarter(b2BProcesslog.getCreateDt()));
                // 请求获取工单明细
                ResponseBody<OrderInfoResponseData> responseBody =null;
                try{
                    // 记录原始数据
                    b2BProcesslogMapper.insert(b2BProcesslog);
                    b2BProcesslog.setResultJson(responseBody.getOriginalJson());
                    // 判断是否请求成功
                    if(responseBody != null && responseBody.getErrorCode() != null &&
                            responseBody.getErrorCode().equals(VattiUtils.SUCCESS_CODE)) {
                        b2BProcesslog.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_SUCESS.value);
                        b2BProcesslogMapper.updateProcessFlag(b2BProcesslog);
                        OrderInfoResponseData orderInfoResponseData = responseBody.getData();
                        // 判断是否有工单明细
                        if(orderInfoResponseData != null && orderInfoResponseData.getData() != null &&
                                orderInfoResponseData.getData().size() > 0 ){

                            VattiOrderInfo vattiOrderInfo = orderInfoResponseData.getData().get(0);
                            vattiOrderInfo.preInsert();
                            vattiOrderInfo.setCreateById(1L);
                            vattiOrderInfo.setUpdateById(1L);
                            vattiOrderInfo.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_ACCEPT.value);
                            vattiOrderInfo.setProcessTime(0);
                            vattiOrderInfo.setQuarter(QuarterUtils.getQuarter(vattiOrderInfo.getCreateDt()));

                            vattiOrderInfoMapper.insertOrderInfo(vattiOrderInfo);

                            MQB2BOrderMessage.B2BOrderMessage.Builder builder = MQB2BOrderMessage.B2BOrderMessage.newBuilder()
                                    .setId(vattiOrderInfo.getId())
                                    .setDataSource(B2BDataSourceEnum.VATTI.id)
                                    .setOrderNo(vattiOrderInfo.getGuid())
                                    .setShopId(B2BShopEnum.VATTI.id)
                                    .setUserName(vattiOrderInfo.getName())
                                    .setUserPhone(vattiOrderInfo.getTel1Number())
                                    .setUserProvince(vattiOrderInfo.getRegion())
                                    .setUserCity(vattiOrderInfo.getCityNo())
                                    .setUserAddress(vattiOrderInfo.getStreet())
                                    .setRemarks(vattiOrderInfo.getRemarks())
                                    .setCreateById(vattiOrderInfo.getCreateById())
                                    .setStatus(1)
                                    .setIssueBy("")
                                    .setQuarter(vattiOrderInfo.getQuarter());
                            MQB2BOrderMessage.B2BOrderItem b2BOrderItem = MQB2BOrderMessage.B2BOrderItem.newBuilder()
                                    .setProductName("")
                                    .setProductSpec("")
                                    .setProductCode(vattiOrderInfo.getProcessType().equals("ZIC3")?vattiOrderInfo.getProductCategory():vattiOrderInfo.getProductCategory2())
                                    .setServiceType(vattiOrderInfo.getProcessType())
                                    .setWarrantyType(VattiUtils.INWARRANTY)
                                    .setQty(1)
                                    .build();
                            builder.addB2BOrderItem(b2BOrderItem);
                            MQB2BOrderMessage.B2BOrderMessage b2BOrderMessage = builder.build();
                            //调用转单队列
                            b2BOrderMQSender.send(b2BOrderMessage);
                        }
                    }else{
                        b2BProcesslog.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_FAILURE.value);
                        String error = responseBody.getErrorMsg() != null ? responseBody.getErrorMsg() :"";
                        error = StringUtils.left(error , 200);
                        b2BProcesslog.setProcessComment(error);
                        b2BProcesslogMapper.updateProcessFlag(b2BProcesslog);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    log.error("新增工单失败", e.getMessage());
                    SysLog sysLog = new SysLog();
                    sysLog.setCreateDt(System.currentTimeMillis());
                    sysLog.setType(1);
                    sysLog.setCreateById(1L);
                    sysLog.setParams(responseBody.getOriginalJson());
                    String error = e.getMessage();
                    error = StringUtils.left(error , 200);
                    sysLog.setException( error );
                    sysLog.setTitle("新增工单失败");
                    sysLog.setQuarter(QuarterUtils.getQuarter(sysLog.getCreateDt()));
                    sysLogMapper.insert(sysLog);
                }
            }
        }
    }

    public VattiOrderInfo findOrderInfoByGuid(String guid) {
        return vattiOrderInfoMapper.findOrderInfoByGuid(guid);
    }

    public List<VattiOrderInfo> findOrdersProcessFlag(List<B2BOrderTransferResult> orderNos) {
        return vattiOrderInfoMapper.findOrdersProcessFlag(orderNos);
    }

    @Transactional
    public void updateTransferResult(List<VattiOrderInfo> wis) {
        for(VattiOrderInfo orderInfo:wis) {
            orderInfo.preUpdate();
            vattiOrderInfoMapper.updateTransferResult(orderInfo);
        }
    }

    public MSPage<B2BOrder> getList(B2BOrderSearchModel workcardSearchModel) {
        if (workcardSearchModel.getPage() != null) {
            PageHelper.startPage(workcardSearchModel.getPage().getPageNo(), workcardSearchModel.getPage().getPageSize());
            Page<VattiOrderInfo> orderInfoPage = vattiOrderInfoMapper.getList(workcardSearchModel);
            Page<B2BOrder> customerPoPage = new Page<>();
            for(VattiOrderInfo orderInfo:orderInfoPage){
                B2BOrder customerPo = new B2BOrder();
                //数据源
                customerPo.setId(orderInfo.getId());
                customerPo.setB2bOrderId(orderInfo.getId());
                customerPo.setDataSource(B2BDataSourceEnum.VATTI.id);
                customerPo.setOrderNo(orderInfo.getGuid());
                customerPo.setParentBizOrderId(orderInfo.getObjectId());
                //华帝店铺
                customerPo.setShopId(B2BShopEnum.VATTI.id);
                customerPo.setUserAddress(orderInfo.getRegion()+orderInfo.getCity()+orderInfo.getStreet());
                customerPo.setUserMobile(orderInfo.getTel1Number());
                customerPo.setUserPhone(orderInfo.getTel2Number());
                customerPo.setStatus(1);
                customerPo.setProcessFlag(orderInfo.getProcessFlag());
                customerPo.setProcessTime(orderInfo.getProcessTime());
                customerPo.setProcessComment(orderInfo.getProcessComment());
                customerPo.setQuarter(orderInfo.getQuarter());
                customerPo.setDescription(orderInfo.getReasonDescription());
                customerPo.setUserName(orderInfo.getName());
                //订单项
                B2BOrder.B2BOrderItem orderItem = new B2BOrder.B2BOrderItem();
                String processType = orderInfo.getProcessType();
                String productCode = "ZIC3".equals(processType) ? orderInfo.getProductCategory() : orderInfo.getProductCategory2();
                orderItem.setQty(1);
                orderItem.setWarrantyType(VattiUtils.INWARRANTY);
                orderItem.setServiceType(processType);
                orderItem.setProductCode(productCode);
                orderItem.setProductName(productCode);
                customerPo.getItems().add(orderItem);
                customerPoPage.add(customerPo);
            }
            MSPage<B2BOrder> returnPage = new MSPage<>();
            returnPage.setPageNo(orderInfoPage.getPageNum());
            returnPage.setPageSize(orderInfoPage.getPageSize());
            returnPage.setPageCount(orderInfoPage.getPages());
            returnPage.setRowCount((int) orderInfoPage.getTotal());
            returnPage.setList(customerPoPage.getResult());
            return returnPage;
        }else {
            return null;
        }
    }

    /**
     *
     * @param agendaOrders
     */
    public void saveVattiOrder(List<AgendaOrderResponseData.AgendaOrder> agendaOrders){
        for(AgendaOrderResponseData.AgendaOrder agendaOrder : agendaOrders){
            String guid = agendaOrder.getGuid();
            Long id = vattiOrderInfoMapper.findOrderByGuid(guid);
            // 判断是否存在该工单
            if(id == null){
                try{
                    VattiOrderInfo vattiOrderInfo = new VattiOrderInfo();
                    vattiOrderInfo.preInsert();
                    vattiOrderInfo.setCreateById(1L);
                    vattiOrderInfo.setUpdateById(1L);
                    vattiOrderInfo.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_ACCEPT.value);
                    vattiOrderInfo.setProcessTime(0);
                    vattiOrderInfo.setQuarter(QuarterUtils.getQuarter(new Date()));
                    vattiOrderInfo.setObjectId(agendaOrder.getObjectId());
                    vattiOrderInfo.setProcessType(agendaOrder.getProcessType());
                    vattiOrderInfo.setGuid(agendaOrder.getGuid());
                    vattiOrderInfo.setRegion(agendaOrder.getZzfld00006y());
                    vattiOrderInfo.setCityNo(agendaOrder.getZzfld00006z());
                    vattiOrderInfo.setStreet(agendaOrder.getStreet());
                    vattiOrderInfo.setCityNo(agendaOrder.getCity());
                    vattiOrderInfo.setCity(agendaOrder.getCity());
                    vattiOrderInfo.setRegion(agendaOrder.getRegiontxt());
                    vattiOrderInfo.setEngineer(agendaOrder.getZzfld000054());
                    vattiOrderInfo.setBuyShop(agendaOrder.getZzfld00001o());
                    vattiOrderInfo.setBuyType(agendaOrder.getZzfld00001m());
                    vattiOrderInfo.setBuyerDate(agendaOrder.getZzfld00002y());
                    vattiOrderInfo.setBarCode(agendaOrder.getZzfld00001r());
                    vattiOrderInfo.setAppointmentDate(agendaOrder.getZzfld00003j());
                    vattiOrderInfo.setAppointmentTime(agendaOrder.getZzfld00001v());
                    vattiOrderInfo.setChangedAt(agendaOrder.getChangedAt());
                    vattiOrderInfo.setChangedBy(agendaOrder.getChangedBy());
                    vattiOrderInfo.setChangedDat(agendaOrder.getChangedDat());
                    vattiOrderInfo.setIvDealer(agendaOrder.getIvDealer());
                    vattiOrderInfo.setIvDealer2(agendaOrder.getIvDealer2());
                    vattiOrderInfo.setKeyGuid(agendaOrder.getKeyGuid());
                    vattiOrderInfo.setInformationSources(agendaOrder.getZzfld00001k());
                    vattiOrderInfo.setPostingDate(agendaOrder.getPostingDate());
                    vattiOrderInfo.setFailurReason(agendaOrder.getZzfld00005b());
                    vattiOrderInfo.setFailurPhen(agendaOrder.getZzfld00005a());
                    vattiOrderInfo.setServiceCity(agendaOrder.getZzfld00006z());
                    vattiOrderInfo.setServiceProvince(agendaOrder.getZzfld00006y());
                    vattiOrderInfo.setTel1Number(agendaOrder.getTel1Numbr());
                    vattiOrderInfo.setTel2Number(agendaOrder.getTel1Numbr());
                    vattiOrderInfo.setName(agendaOrder.getName());
                    vattiOrderInfo.setZwillOverDat(agendaOrder.getZwillOverDat());
                    vattiOrderInfo.setZwillOverLei(agendaOrder.getZwillOverLei());
                    vattiOrderInfo.setZwillOverTim(agendaOrder.getZwillOverTim());
                    vattiOrderInfo.setCreateDt(System.currentTimeMillis());
                    vattiOrderInfo.setUpdateDt(System.currentTimeMillis());
                    vattiOrderInfo.setCreateBy(1L);
                    vattiOrderInfo.setUpdateBy(1L);
                    vattiOrderInfo.setDistrict(agendaOrder.getDistrict());
                    vattiOrderInfo.setProductCategory(agendaOrder.getZzfld000057());
                    vattiOrderInfo.setProductCategory2(agendaOrder.getZzfld000059());
                    vattiOrderInfoMapper.insertOrderInfo(vattiOrderInfo);
                    B2BVattiProperties.DataSourceConfig dataSourceConfig = vattiProperties.getDataSourceConfig();
                    if(dataSourceConfig != null && dataSourceConfig.getOrderMqEnabled()) {
                        sendMQ(vattiOrderInfo);
                    }
                }catch (Exception e){
                    log.error("新增工单失败:{}", e.getMessage());
                    insertSysLog(guid, e);
                }
            }
        }
    }



    public MSResponse process(String guid,String objectId) {
        MSResponse msResponse = new MSResponse(MSErrorCode.SUCCESS);
        if (StringUtils.isBlank(guid)) {
            msResponse.setCode(-1);
            msResponse.setMsg("工单唯一编号不能为空！");
            return msResponse;
        }
        if (StringUtils.isBlank(objectId)) {
            msResponse.setCode(-1);
            msResponse.setMsg("工单号不能为空！");
            return msResponse;
        }
        Long id = vattiOrderInfoMapper.findOrderByGuid(guid);
        if(id != null){
            return msResponse;
        }
        // 请求工单详情
        B2BVattiProperties.DataSourceConfig dataSourceConfig =
                vattiProperties.getDataSourceConfig();
        String url = dataSourceConfig.getRequestMainUrl().concat("/").concat(OperationCommand.OperationCode.ORDER.apiUrl);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("orderId", objectId);
        B2BOrderProcesslog processlog = b2BProcesslogService.savelog
                (OperationCommand.OperationCode.ORDER.apiUrl, "{\"orderId\":\"" + objectId + "\"}");
        ResponseBody<OrderDetailResponse> responseBody =
                okHttpUtils.getSyncGenericNew(urlBuilder.build(), OrderDetailResponse.class, true);
        try {
            processlog.setResultJson(responseBody.getOriginalJson());
            if (responseBody != null && responseBody.getErrorCode() != null &&
                    responseBody.getErrorCode().equals(VattiUtils.SUCCESS_CODE)) {
                processlog.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_SUCESS.value);
                OrderDetailResponse data = responseBody.getData();
                List<OrderDetail> data1 = data.getData();
                boolean flag = false;
                if(data1 != null) {
                    for (OrderDetail detail : data1) {
                        if (guid.equals(detail.getGuid())) {
                            flag = true;
                            if (id == null) {
                                inserOrder(dataSourceConfig, detail);
                            }
                            break;
                        }
                    }
                }
                if(!flag){
                    msResponse.setCode(1);
                    msResponse.setMsg("没有找到匹配GUID:"+guid+"的工单详情");
                }
                b2BProcesslogService.updateProcessFlag(processlog);
            }else{
                processlog.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_FAILURE.value);
                processlog.setProcessComment(responseBody.getErrorMsg());
                b2BProcesslogService.updateProcessFlag(processlog);
                msResponse.setCode(1);
                msResponse.setMsg("工单详情读取失败！");
            }
        }catch(Exception e){
            log.error("工单处理失败:{}", e.getMessage());
            insertSysLog(guid, e);
            msResponse.setCode(1);
            msResponse.setMsg("数据库异常！");
            return msResponse;
        }
        return msResponse;
    }

    public void inserOrder(B2BVattiProperties.DataSourceConfig dataSourceConfig, OrderDetail agendaOrder) {
        // 判断是否存在该工单
        VattiOrderInfo vattiOrderInfo = new VattiOrderInfo();
        vattiOrderInfo.preInsert();
        vattiOrderInfo.setCreateById(1L);
        vattiOrderInfo.setUpdateById(1L);
        vattiOrderInfo.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_ACCEPT.value);
        vattiOrderInfo.setProcessTime(0);
        vattiOrderInfo.setQuarter(QuarterUtils.getQuarter(new Date()));
        vattiOrderInfo.setObjectId(agendaOrder.getObjectId());
        vattiOrderInfo.setProcessType(agendaOrder.getProcessType());
        vattiOrderInfo.setGuid(agendaOrder.getGuid());
        vattiOrderInfo.setRegionNo(agendaOrder.getRegion());
        vattiOrderInfo.setCityNo(agendaOrder.getCityNo());
        vattiOrderInfo.setDistrictNo(agendaOrder.getDistrctNo());
        vattiOrderInfo.setStreet(agendaOrder.getStreet());
        //查询对应的地址信息
        searchAddr(vattiOrderInfo);
        vattiOrderInfo.setEngineer(agendaOrder.getZzfld000054());
        vattiOrderInfo.setBuyShop(agendaOrder.getZzfld00001o());
        vattiOrderInfo.setBuyType(agendaOrder.getZzfld00001m());
        vattiOrderInfo.setBuyerDate(agendaOrder.getZzfld00002y());
        vattiOrderInfo.setBarCode(agendaOrder.getZzfld00001r());
        vattiOrderInfo.setAppointmentDate(agendaOrder.getZzfld00003j());
        vattiOrderInfo.setAppointmentTime(agendaOrder.getZzfld00001v());
        vattiOrderInfo.setChangedAt(agendaOrder.getChangedAt());
//        vattiOrderInfo.setChangedBy(agendaOrder.getChangedBy());
//        vattiOrderInfo.setChangedDat(agendaOrder.getChangedDat());
//        vattiOrderInfo.setIvDealer(agendaOrder.getIvDealer());
//        vattiOrderInfo.setIvDealer2(agendaOrder.getIvDealer2());
//        vattiOrderInfo.setKeyGuid(agendaOrder.getKeyGuid());
        vattiOrderInfo.setInformationSources(agendaOrder.getZzfld00001k());
        vattiOrderInfo.setPostingDate(agendaOrder.getPostingDate());
        vattiOrderInfo.setFailurReason(agendaOrder.getZzfld00005b());
        vattiOrderInfo.setFailurPhen(agendaOrder.getZzfld00005a());
        vattiOrderInfo.setServiceCity(agendaOrder.getZzfld00006z());
        vattiOrderInfo.setServiceProvince(agendaOrder.getZzfld00006y());
        vattiOrderInfo.setTel1Number(agendaOrder.getTel1Numbr());
        vattiOrderInfo.setTel2Number(agendaOrder.getTel1Numbr());
        vattiOrderInfo.setName(agendaOrder.getName());
//        vattiOrderInfo.setZwillOverDat(agendaOrder.getZwillOverDat());
//        vattiOrderInfo.setZwillOverLei(agendaOrder.getZwillOverLei());
//        vattiOrderInfo.setZwillOverTim(agendaOrder.getZwillOverTim());
        vattiOrderInfo.setCreateDt(System.currentTimeMillis());
        vattiOrderInfo.setUpdateDt(System.currentTimeMillis());
        vattiOrderInfo.setCreateBy(1L);
        vattiOrderInfo.setUpdateBy(1L);
        vattiOrderInfo.setProductCategory(agendaOrder.getZzfld000057());
        vattiOrderInfo.setProductCategory2(agendaOrder.getZzfld000059());
        vattiOrderInfoMapper.insertOrderInfo(vattiOrderInfo);
        if(dataSourceConfig != null && dataSourceConfig.getOrderMqEnabled()) {
            sendMQ(vattiOrderInfo);
        }
    }

    public void insertSysLog(String guid, Exception e) {
        SysLog sysLog = new SysLog();
        sysLog.setCreateDt(System.currentTimeMillis());
        sysLog.setType(1);
        sysLog.setCreateById(1L);
        sysLog.setParams(guid);
        String error = StringUtils.left(e.getMessage(), 200);
        sysLog.setException(error);
        sysLog.setTitle("新增工单失败");
        sysLog.setQuarter(QuarterUtils.getQuarter(sysLog.getCreateDt()));
        sysLogMapper.insert(sysLog);
    }

    /**
     * 查询地址名称
     * @param vattiOrderInfo
     */
    private void searchAddr(VattiOrderInfo vattiOrderInfo) {
        String regionNo = vattiOrderInfo.getRegionNo();
        regionNo = regionNo.replaceAll("^(0+)", "");
        String cityNo = vattiOrderInfo.getCityNo();
        cityNo = cityNo.replaceAll("^(0+)", "");
        String districtNo = vattiOrderInfo.getDistrictNo();
        districtNo = districtNo.replaceAll("^(0+)", "");
        List<VattiArea> areas = areaService.findByCode(regionNo,cityNo,districtNo);
        Map<String, String> collect = areas.stream().collect(
                Collectors.toMap(VattiArea::getCode,VattiArea::getName, (key1, key2) -> key2));
        vattiOrderInfo.setRegion(collect.get(regionNo));
        vattiOrderInfo.setDistrict(collect.get(districtNo));
        vattiOrderInfo.setCity(collect.get(cityNo));
    }

    private void sendMQ(VattiOrderInfo vattiOrderInfo) {
        MQB2BOrderMessage.B2BOrderMessage.Builder builder = MQB2BOrderMessage.B2BOrderMessage.newBuilder()
                .setId(vattiOrderInfo.getId())
                .setDataSource(B2BDataSourceEnum.VATTI.id)
                .setOrderNo(vattiOrderInfo.getGuid())
                .setParentBizOrderId(vattiOrderInfo.getObjectId())
                .setShopId(B2BShopEnum.VATTI.id)
                .setUserName(vattiOrderInfo.getName())
                .setUserPhone(vattiOrderInfo.getTel1Number())
                .setUserProvince(vattiOrderInfo.getRegion())
                .setUserCity(vattiOrderInfo.getCityNo())
                .setUserAddress(vattiOrderInfo.getStreet())
                .setRemarks(vattiOrderInfo.getRemarks())
                .setStatus(1)
                .setIssueBy("")
                .setQuarter(vattiOrderInfo.getQuarter());
        MQB2BOrderMessage.B2BOrderItem b2BOrderItem = MQB2BOrderMessage.B2BOrderItem.newBuilder()
                .setProductCode
                        (vattiOrderInfo.getProcessType().equals("ZIC3") ? vattiOrderInfo.getProductCategory() : vattiOrderInfo.getProductCategory2())
                .setServiceType(vattiOrderInfo.getProcessType())
                .setWarrantyType(VattiUtils.INWARRANTY)
                .setQty(1)
                .build();
        builder.addB2BOrderItem(b2BOrderItem);
        MQB2BOrderMessage.B2BOrderMessage b2BOrderMessage = builder.build();
        //调用转单队列
        b2BOrderMQSender.send(b2BOrderMessage);
    }
}
