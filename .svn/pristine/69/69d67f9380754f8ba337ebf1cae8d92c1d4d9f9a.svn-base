package com.kkl.kklplus.b2b.vatti.controller;


import com.google.gson.Gson;
import com.kkl.kklplus.b2b.vatti.entity.VattiOrderInfo;
import com.kkl.kklplus.b2b.vatti.entity.VattiUpdateOrder;
import com.kkl.kklplus.b2b.vatti.http.command.OperationCommand;
import com.kkl.kklplus.b2b.vatti.http.request.ModeifyInstallOrderRequestParam;
import com.kkl.kklplus.b2b.vatti.http.request.ModeifyRepairOrderRequestParam;
import com.kkl.kklplus.b2b.vatti.http.response.ResponseBody;
import com.kkl.kklplus.b2b.vatti.http.utils.OkHttpUtils;
import com.kkl.kklplus.b2b.vatti.service.B2BProcesslogService;
import com.kkl.kklplus.b2b.vatti.service.SysLogService;
import com.kkl.kklplus.b2b.vatti.service.VattiOrderInfoService;
import com.kkl.kklplus.b2b.vatti.service.VattiUpdateOrderService;
import com.kkl.kklplus.b2b.vatti.utils.VattiUtils;
import com.kkl.kklplus.common.exception.MSErrorCode;
import com.kkl.kklplus.common.response.MSResponse;
import com.kkl.kklplus.entity.b2b.common.B2BProcessFlag;
import com.kkl.kklplus.entity.b2bcenter.rpt.B2BOrderProcesslog;
import com.kkl.kklplus.utils.QuarterUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/vattiUpdateOrder")
public class VattiUpdateOrderController {


    @Autowired
    private VattiOrderInfoService vattiOrderInfoService;

    @Autowired
    private VattiUpdateOrderService vattiUpdateOrderService;

    @Autowired
    private B2BProcesslogService b2BProcesslogService;

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private OkHttpUtils okHttpUtils;

    /**
     * 更改工单信息
     * @return
     */
    @RequestMapping("updateOrderMessage")
    public MSResponse updateOrderMessage(@RequestBody VattiUpdateOrder vattiUpdateOrder) {
        MSResponse response = new MSResponse(MSErrorCode.SUCCESS);
        String guid = vattiUpdateOrder.getGuid();
        if(StringUtils.isEmpty(guid)){
            response.setErrorCode(MSErrorCode.FAILURE);
            response.setMsg("guid为必填项！");
            return response;
        }
        VattiOrderInfo vattiOrderInfo = vattiOrderInfoService.findOrderInfoByGuid(guid);
        if(vattiOrderInfo == null){
            response.setErrorCode(MSErrorCode.FAILURE);
            response.setMsg("工单不存在！");
            return response;
        }
        Gson gson = new Gson();
        B2BOrderProcesslog b2BProcesslog = new B2BOrderProcesslog();
        b2BProcesslog.preInsert();
        b2BProcesslog.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_ACCEPT.value);
        b2BProcesslog.setProcessTime(0);
        b2BProcesslog.setCreateById(1L);
        b2BProcesslog.setUpdateById(1L);
        b2BProcesslog.setQuarter(QuarterUtils.getQuarter(b2BProcesslog.getCreateDate()));
        String processType = vattiOrderInfo.getProcessType();
        ResponseBody<ResponseBody> resBody = null;
        try {
            if (VattiUtils.INSTALL.equals(processType)) {
                ModeifyInstallOrderRequestParam modeifyInstallOrderRequestParam =
                        vattiUpdateOrderService.disposeInstallOrderMessage(vattiUpdateOrder, vattiOrderInfo);
                b2BProcesslog.setInfoJson(gson.toJson(modeifyInstallOrderRequestParam));
                b2BProcesslog.setInterfaceName(OperationCommand.OperationCode.MODEIFYINSTALLSERVICEORDER.apiUrl);
                b2BProcesslogService.insert(b2BProcesslog);
                OperationCommand command = OperationCommand.newInstance
                        (OperationCommand.OperationCode.MODEIFYINSTALLSERVICEORDER, modeifyInstallOrderRequestParam);
                resBody = okHttpUtils.postSyncGenericNew(command, ResponseBody.class);
            } else if (VattiUtils.REPAIR.equals(processType)) {
                ModeifyRepairOrderRequestParam modeifyRepairOrderRequestParam =
                        vattiUpdateOrderService.disposeRepairOrderMessage(vattiUpdateOrder, vattiOrderInfo);
                b2BProcesslog.setInfoJson(gson.toJson(modeifyRepairOrderRequestParam));
                b2BProcesslog.setInterfaceName(OperationCommand.OperationCode.MODEIFYREPAIRSERVICEORDER.apiUrl);
                b2BProcesslogService.insert(b2BProcesslog);
                OperationCommand command = OperationCommand.newInstance
                        (OperationCommand.OperationCode.MODEIFYREPAIRSERVICEORDER, modeifyRepairOrderRequestParam);
                resBody = okHttpUtils.postSyncGenericNew(command, ResponseBody.class);
            }
            vattiUpdateOrder.preInsert();
            vattiUpdateOrder.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_ACCEPT.value);
            vattiUpdateOrder.setProcessTime(0);
            vattiUpdateOrder.setCreateById(1L);
            vattiUpdateOrder.setUpdateById(1L);
            vattiUpdateOrder.setQuarter(QuarterUtils.getQuarter(vattiUpdateOrder.getCreateDate()));
            vattiUpdateOrderService.insert(vattiUpdateOrder);
            b2BProcesslog.setResultJson(resBody.getOriginalJson());
            if (resBody != null && resBody.getErrorCode() != ResponseBody.ErrorCode.SUCCESS.code) {
                if (resBody.getErrorCode() >= ResponseBody.ErrorCode.REQUEST_INVOCATION_FAILURE.code) {
                    response.setErrorCode(new MSErrorCode(resBody.getErrorCode(),
                            StringUtils.left(resBody.getErrorMsg(), 200)));
                }
                response.setThirdPartyErrorCode(new MSErrorCode(resBody.getErrorCode(),
                        StringUtils.left(resBody.getErrorMsg(), 200)));
                b2BProcesslog.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_FAILURE.value);
                b2BProcesslog.setProcessComment(StringUtils.left(resBody.getErrorMsg(), 200));
                b2BProcesslogService.updateProcessFlag(b2BProcesslog);
                vattiUpdateOrder.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_FAILURE.value);
                vattiUpdateOrder.setProcessComment(StringUtils.left(resBody.getErrorMsg(), 200));
                vattiUpdateOrderService.updateProcessFlag(vattiUpdateOrder);
                return response;
            } else {
                b2BProcesslog.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_SUCESS.value);
                b2BProcesslogService.updateProcessFlag(b2BProcesslog);
                vattiUpdateOrder.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_SUCESS.value);
                vattiUpdateOrderService.updateProcessFlag(vattiUpdateOrder);
                return response;
            }
        }catch (Exception e) {
            String error = StringUtils.left(e.getMessage(),200);
            response.setErrorCode(MSErrorCode.FAILURE);
            response.setMsg(error);
            b2BProcesslog.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_FAILURE.value);
            b2BProcesslog.setProcessComment(error);
            b2BProcesslogService.updateProcessFlag(b2BProcesslog);
            log.error("工单操作失败", e.getMessage());
            sysLogService.insert(1L,gson.toJson(vattiUpdateOrder),error,
                    error, "updateOrderMessage", "POST");
            return response;
        }
    }

}
