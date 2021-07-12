package com.kkl.kklplus.b2b.vatti.controller;

import com.google.gson.Gson;
import com.kkl.kklplus.b2b.vatti.entity.VattiArea;
import com.kkl.kklplus.b2b.vatti.entity.VattiOrderInfo;
import com.kkl.kklplus.b2b.vatti.http.command.OperationCommand;
import com.kkl.kklplus.b2b.vatti.http.config.B2BVattiProperties;
import com.kkl.kklplus.b2b.vatti.http.response.AgendaOrderResponseData;
import com.kkl.kklplus.b2b.vatti.http.response.AreaResponse;
import com.kkl.kklplus.b2b.vatti.http.response.ResponseBody;
import com.kkl.kklplus.b2b.vatti.http.utils.OkHttpUtils;
import com.kkl.kklplus.b2b.vatti.service.B2BProcesslogService;
import com.kkl.kklplus.b2b.vatti.service.SysLogService;
import com.kkl.kklplus.b2b.vatti.service.VattiAreaService;
import com.kkl.kklplus.b2b.vatti.service.VattiOrderInfoService;
import com.kkl.kklplus.b2b.vatti.utils.VattiUtils;
import com.kkl.kklplus.common.exception.MSErrorCode;
import com.kkl.kklplus.common.response.MSResponse;
import com.kkl.kklplus.entity.b2b.common.B2BProcessFlag;
import com.kkl.kklplus.entity.b2bcenter.rpt.B2BOrderProcesslog;
import com.kkl.kklplus.entity.b2bcenter.sd.B2BOrder;
import com.kkl.kklplus.entity.b2bcenter.sd.B2BOrderSearchModel;
import com.kkl.kklplus.entity.b2bcenter.sd.B2BOrderTransferResult;
import com.kkl.kklplus.entity.common.MSPage;
import com.kkl.kklplus.utils.QuarterUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/orderInfo")
public class VattiOrderInfoController {

    @Autowired
    private B2BProcesslogService b2BProcesslogService;

    @Autowired
    private VattiOrderInfoService vattiOrderInfoService;

    @Autowired
    private SysLogService sysLogService;


    @Autowired
    private B2BVattiProperties vattiProperties;

    @Autowired
    private OkHttpUtils okHttpUtils;

    @Scheduled(cron = "0 */10 * * * ?")
    public void vattiJob() {
        B2BVattiProperties.DataSourceConfig dataSourceConfig =
                vattiProperties.getDataSourceConfig();
        if (!dataSourceConfig.getScheduleEnabled()) {
            return;
        }
        String url = dataSourceConfig.getRequestMainUrl().concat("/").concat(OperationCommand.OperationCode.TODOLIST.apiUrl);
        HttpUrl.Builder urlBuilder =HttpUrl.parse(url).newBuilder();
        String companyId = dataSourceConfig.getCompanyId();
        String ivDealer2 = dataSourceConfig.getIvDealer2();
        String userId = dataSourceConfig.getUserId();
        if(StringUtils.isNotBlank(companyId)){
            urlBuilder.addQueryParameter("companyId", companyId);
        }
        if(StringUtils.isNotBlank(ivDealer2)){
            urlBuilder.addQueryParameter("ivDealer2", ivDealer2);
        }
        if(StringUtils.isNotBlank(userId)){
            urlBuilder.addQueryParameter("userId", userId);
        }
        ResponseBody<AgendaOrderResponseData> responseBody =
                okHttpUtils.getSyncGenericNew(urlBuilder.build(),AgendaOrderResponseData.class);
        B2BOrderProcesslog b2BProcesslog = new B2BOrderProcesslog();
        b2BProcesslog.preInsert();
        b2BProcesslog.setInterfaceName(OperationCommand.OperationCode.TODOLIST.apiUrl);
        b2BProcesslog.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_ACCEPT.value);
        b2BProcesslog.setProcessTime(0);
        b2BProcesslog.setCreateById(1L);
        b2BProcesslog.setUpdateById(1L);
        b2BProcesslog.setQuarter(QuarterUtils.getQuarter(b2BProcesslog.getCreateDate()));
        b2BProcesslogService.insert(b2BProcesslog);
        b2BProcesslog.setResultJson(responseBody.getOriginalJson());
        if(responseBody != null && responseBody.getErrorCode() != null &&
                responseBody.getErrorCode().equals(VattiUtils.SUCCESS_CODE)){
            b2BProcesslog.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_SUCESS.value);
            b2BProcesslogService.updateProcessFlag(b2BProcesslog);
            AgendaOrderResponseData agendaOrder = responseBody.getData();
            if(agendaOrder != null && agendaOrder.getData() != null && agendaOrder.getData().size() > 0){
                vattiOrderInfoService.saveVattiOrder(agendaOrder.getData());
            }
        }else{
            b2BProcesslog.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_FAILURE.value);
            b2BProcesslog.setProcessComment(responseBody.getErrorMsg());
            b2BProcesslogService.updateProcessFlag(b2BProcesslog);
        }

    }
    @PostMapping("/getList")
    public MSResponse<MSPage<B2BOrder>> getList(@RequestBody B2BOrderSearchModel workcardSearchModel) {
        Gson gson = new Gson();
        try {
            MSPage<B2BOrder> returnPage = vattiOrderInfoService.getList(workcardSearchModel);
            return new MSResponse<>(MSErrorCode.SUCCESS, returnPage);
        } catch (Exception e) {
            log.error("查询工单失败:{}", e.getMessage());
            sysLogService.insert(1L,gson.toJson(workcardSearchModel),
                    "查询工单失败：" + e.getMessage(),
                    "查询工单失败", VattiUtils.ORDERLIST, VattiUtils.REQUESTMETHOD);
            return new MSResponse<>(new MSErrorCode(1000, StringUtils.left(e.getMessage(),200)));
        }
    }
    /**
     * 检查工单是否可以转换
     * @param orderNos
     * @return
     */
    @PostMapping("/checkWorkcardProcessFlag")
    public MSResponse checkWorkcardProcessFlag(@RequestBody List<B2BOrderTransferResult> orderNos){
        try {
            if(orderNos == null){
                return new MSResponse(new MSErrorCode(1000, "参数错误，工单编号不能为空"));
            }
            //查询出对应工单的状态
            List<VattiOrderInfo> orderInfos = vattiOrderInfoService.findOrdersProcessFlag(orderNos);
            if(orderInfos == null){
                return new MSResponse(MSErrorCode.FAILURE);
            }
            for (VattiOrderInfo orderInfo : orderInfos) {
                if (orderInfo.getProcessFlag() != null && orderInfo.getProcessFlag() == B2BProcessFlag.PROCESS_FLAG_SUCESS.value) {
                    return new MSResponse(new MSErrorCode(1000, orderInfo.getGuid()+"工单已经转换成功,不能重复转换"));
                }
            }
            return new MSResponse(MSErrorCode.SUCCESS);
        }catch (Exception e){
            log.error("检查工单失败:{}", e.getMessage());
            sysLogService.insert(1L,new Gson().toJson(orderNos),"检查工单失败：" + e.getMessage(),
                    "检查工单失败",VattiUtils.CHECKPROCESSFLAG, VattiUtils.REQUESTMETHOD);
            return new MSResponse(new MSErrorCode(1000, StringUtils.left(e.getMessage(),200)));
        }
    }

    @PostMapping("/updateTransferResult")
    public MSResponse updateTransferResult(@RequestBody List<B2BOrderTransferResult> workcardTransferResults) {
        try {
            // 将List转化为Map 以b2bOrderNO为键
            Map<Long, List<B2BOrderTransferResult>> collect =
                    workcardTransferResults.stream().collect(Collectors.groupingBy(B2BOrderTransferResult::getB2bOrderId));
            //查询出需要转换的工单
            List<VattiOrderInfo> orderInfos = vattiOrderInfoService.findOrdersProcessFlag(workcardTransferResults);
            //用来存放各个数据源转换成功的数量
            int count = 0;
            //存放需要转换的工单集合
            List<VattiOrderInfo> wis = new ArrayList<>();
            for (VattiOrderInfo vattiOrderInfo : orderInfos) {
                //如果工单为转换成功的才存放进工单集合
                if (vattiOrderInfo.getProcessFlag() != B2BProcessFlag.PROCESS_FLAG_SUCESS.value) {
                    List<B2BOrderTransferResult> transferResults = collect.get(vattiOrderInfo.getId());
                    if (transferResults != null && transferResults.size() > 0) {
                        B2BOrderTransferResult bOrderTransferResult = transferResults.get(0);
                        //成功转换的才计算
                        if (bOrderTransferResult.getProcessFlag() == B2BProcessFlag.PROCESS_FLAG_SUCESS.value) {
                            count++;
                        }
                        vattiOrderInfo.setProcessFlag(bOrderTransferResult.getProcessFlag());
                        vattiOrderInfo.setKklOrderId(bOrderTransferResult.getOrderId());
                        vattiOrderInfo.setKklOrderNo(bOrderTransferResult.getKklOrderNo());
                        vattiOrderInfo.setUpdateDt(bOrderTransferResult.getUpdateDt());
                        vattiOrderInfo.setProcessComment(bOrderTransferResult.getProcessComment());
                        wis.add(vattiOrderInfo);
                    }
                }
            }
            vattiOrderInfoService.updateTransferResult(wis);
            return new MSResponse(MSErrorCode.SUCCESS);
        } catch (Exception e) {
            log.error("工单转换失败:{}", e.getMessage());
            sysLogService.insert(1L, new Gson().toJson(workcardTransferResults),
                    "工单转换失败：" + e.getMessage(),
                    "工单转换失败", VattiUtils.UPDATETRANSFERRESULT, VattiUtils.REQUESTMETHOD);
            return new MSResponse(new MSErrorCode(1000, StringUtils.left(e.getMessage(),200)));
        }
    }

    @GetMapping("/testJob")
    public MSResponse testJob(){
        vattiJob();
        return new MSResponse(MSErrorCode.SUCCESS);
    }

    @Autowired
    private VattiAreaService areaService;

    @GetMapping("/saveArea")
    public MSResponse saveArea(){
        String u = "/aftersaleservice/api/address/province";
        B2BVattiProperties.DataSourceConfig dataSourceConfig = vattiProperties.getDataSourceConfig();
        String url = dataSourceConfig.getRequestMainUrl().concat("/").concat(u);
        saveArea(null,url,1);
        return new MSResponse(MSErrorCode.SUCCESS);
    }


    private void saveArea(VattiArea a,String url,int type) {
        if(type == 2){
            url = url.concat("/").concat(a.getCode()).concat("/city");
        }else if(type == 3){
            url = url.concat("/").concat(a.getCode()).concat("/county");
        }
        ResponseBody<AreaResponse> responseBody =
                okHttpUtils.getSyncGenericNew(HttpUrl.parse(url),AreaResponse.class,true);
        AreaResponse data = responseBody.getData();
        List<AreaResponse.Area> areas = data.getData();
        List<VattiArea> vattiAreas = new ArrayList<>();
        for(AreaResponse.Area area : areas){
            VattiArea vattiArea;
            if(a == null){
                vattiArea = areaService.insert(0l, "0", area.getItemCode(),
                        area.getItemName(), area.getItemName(), type, System.currentTimeMillis());
            }else {
                Long id = a.getId();
                vattiArea = areaService.insert(id, a.getParentIds().concat(",").concat(id.toString()), area.getItemCode(),
                        area.getItemName(), a.getFullName().concat(area.getItemName()), type, System.currentTimeMillis());
            }
            vattiAreas.add(vattiArea);
        }
        if(type < 3){
            for(VattiArea area : vattiAreas){
                saveArea(area,url,type + 1);
            }
        }
    }

}
