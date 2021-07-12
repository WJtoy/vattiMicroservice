package com.kkl.kklplus.b2b.vatti.service;

import com.kkl.kklplus.b2b.vatti.entity.VattiOrderInfo;
import com.kkl.kklplus.b2b.vatti.entity.VattiUpdateOrder;
import com.kkl.kklplus.b2b.vatti.http.request.ModeifyInstallOrderRequestParam;
import com.kkl.kklplus.b2b.vatti.http.request.ModeifyRepairOrderRequestParam;
import com.kkl.kklplus.b2b.vatti.mapper.VattiUpdateOrderMapper;
import com.kkl.kklplus.b2b.vatti.utils.VattiStatusCodeEnum;
import com.kkl.kklplus.b2b.vatti.utils.VattiUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Slf4j
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class VattiUpdateOrderService {

    @Resource
    private VattiUpdateOrderMapper vattiUpdateOrderMapper;


    public ModeifyInstallOrderRequestParam disposeInstallOrderMessage(VattiUpdateOrder vattiUpdateOrder, VattiOrderInfo vattiOrderInfo) {

        ModeifyInstallOrderRequestParam modeifyInstallOrderRequestParam = new ModeifyInstallOrderRequestParam();
        modeifyInstallOrderRequestParam.setGuid(vattiOrderInfo.getGuid());
        Integer kklStatus = vattiUpdateOrder.getStatus();
        String status = VattiStatusCodeEnum.get(kklStatus).vattiCode;
        modeifyInstallOrderRequestParam.setStatus_code(status);
        if(VattiStatusCodeEnum.FINISH.equals(status)){
            //modeifyInstallOrderRequestParam.setOrdered_PROD(vattiOrderInfo.getKindCode());
            modeifyInstallOrderRequestParam.setQuantity("1");
            modeifyInstallOrderRequestParam.setUrl_path(vattiUpdateOrder.getUrlPath());
            // 购买渠道
            modeifyInstallOrderRequestParam.setZzfld00001m(vattiOrderInfo.getBuyType());
            // 购买日期
            modeifyInstallOrderRequestParam.setZzfld00002y(vattiOrderInfo.getBuyerDate());
            // 维修性质
            modeifyInstallOrderRequestParam.setZzfld00003h(VattiUtils.INWARRANTY);
            // 网点处理结果
            modeifyInstallOrderRequestParam.setLines_tdline_zic4(vattiUpdateOrder.getDisposeResult());
            Long time = vattiUpdateOrder.getServiceFinishDate();
            Date date = new Date(time);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            // 服务完成日期
            modeifyInstallOrderRequestParam.setZzfld00003j(simpleDateFormat.format(date));
            // 服务完成时间
            modeifyInstallOrderRequestParam.setZzfld00003k(VattiUtils.returnDateCode(time));
            //服务省份
            modeifyInstallOrderRequestParam.setZzfld00006y(vattiUpdateOrder.getServiceProvince());
            //服务城市
            modeifyInstallOrderRequestParam.setZzfld00006z(vattiUpdateOrder.getServiceCity());
        }else if(VattiStatusCodeEnum.CANCEL.equals(status)){
            // 取消原因
            modeifyInstallOrderRequestParam.setZzfld000060("06");
        }else if(VattiStatusCodeEnum.APPOINTMENT.equals(status)){
            Integer i = vattiUpdateOrderMapper.findAppointmentCount(vattiOrderInfo.getGuid());
            // 是否改约
            if(i>1){
                modeifyInstallOrderRequestParam.setZzfld00004m("03");
            }else{
                modeifyInstallOrderRequestParam.setZzfld00004m("02");
            }
            // 改约原因
            modeifyInstallOrderRequestParam.setZzfld00005z("04");
            Long time = vattiUpdateOrder.getAppointmentDate();
            Date date = new Date(time);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            // 预约日期
            modeifyInstallOrderRequestParam.setZzfld00003j(simpleDateFormat.format(date));
            // 预约时间
            modeifyInstallOrderRequestParam.setZzfld00001v(VattiUtils.returnDateCode(time));
            // 网点跟进内容
            modeifyInstallOrderRequestParam.setLines_tdline_zice(vattiUpdateOrder.getFollowUpContent());
        }
        return modeifyInstallOrderRequestParam;
    }

    public ModeifyRepairOrderRequestParam disposeRepairOrderMessage(VattiUpdateOrder vattiUpdateOrder, VattiOrderInfo vattiOrderInfo) {
        ModeifyRepairOrderRequestParam modeifyRepairOrderRequestParam = new ModeifyRepairOrderRequestParam();
        modeifyRepairOrderRequestParam.setGuid(vattiOrderInfo.getGuid());
        Integer kklStatus = vattiUpdateOrder.getStatus();
        String status = VattiStatusCodeEnum.get(kklStatus).vattiCode;
        modeifyRepairOrderRequestParam.setStatus_code(status);
        if(VattiStatusCodeEnum.FINISH.equals(status)){
            //modeifyRepairOrderRequestParam.setOrdered_PROD(vattiOrderInfo.getKindCode());
            modeifyRepairOrderRequestParam.setQuantity("1");
            // 条形码
            modeifyRepairOrderRequestParam.setZzfld00001r(vattiUpdateOrder.getBarCode());
            // 购买渠道
            modeifyRepairOrderRequestParam.setZzfld00001m(vattiOrderInfo.getBuyType());
            // 购买日期
            modeifyRepairOrderRequestParam.setZzfld00002y(vattiOrderInfo.getBuyerDate());
            // 维修性质
            modeifyRepairOrderRequestParam.setZzfld00003h(VattiUtils.INWARRANTY);
            // 服务省份
            modeifyRepairOrderRequestParam.setZzfld00006y(vattiOrderInfo.getRegion());
            // 服务城市
            modeifyRepairOrderRequestParam.setZzfld00006z(vattiOrderInfo.getCityNo());
            // 服务方式
            modeifyRepairOrderRequestParam.setZzfld00003i("01");
            // 故障现象
            modeifyRepairOrderRequestParam.setZzfld00005a(vattiUpdateOrder.getFailurPhen());
            // 故障原因
            modeifyRepairOrderRequestParam.setZzfld00005b(vattiUpdateOrder.getFailurReason());
            // 网点处理结果
            modeifyRepairOrderRequestParam.setLines_tdline_zic4(vattiUpdateOrder.getDisposeResult());
            Long time = vattiUpdateOrder.getServiceFinishDate();
            Date date = new Date(time);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            // 服务完成日期
            modeifyRepairOrderRequestParam.setZzfld00003j(simpleDateFormat.format(date));
            // 服务完成时间
            modeifyRepairOrderRequestParam.setZzfld00003k(VattiUtils.returnDateCode(time));
        }else if(VattiStatusCodeEnum.CANCEL.equals(status)){
            // 取消原因
            modeifyRepairOrderRequestParam.setZzfld000060("06");
        }else if(VattiStatusCodeEnum.APPOINTMENT.equals(status)){
            Integer i = vattiUpdateOrderMapper.findAppointmentCount(vattiOrderInfo.getGuid());
            // 改约原因
            modeifyRepairOrderRequestParam.setZzfld00005z("04");
            // 是否改约
            if(i>1){
                modeifyRepairOrderRequestParam.setZzfld00004m("03");
            }else{
                modeifyRepairOrderRequestParam.setZzfld00004m("02");
            }
            Long time = vattiUpdateOrder.getAppointmentDate();
            Date date = new Date(time);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            // 预约日期
            modeifyRepairOrderRequestParam.setZzfld00003j(simpleDateFormat.format(date));
            // 预约时间
            modeifyRepairOrderRequestParam.setZzfld00001v(VattiUtils.returnDateCode(time));
            // 网点跟进内容
            modeifyRepairOrderRequestParam.setLines_tdline_zice(vattiUpdateOrder.getFollowUpContent());
        }
        return modeifyRepairOrderRequestParam;
    }

    public void insert(VattiUpdateOrder vattiUpdateOrder) {
        vattiUpdateOrderMapper.insert(vattiUpdateOrder);
    }

    public void updateProcessFlag(VattiUpdateOrder vattiUpdateOrder) {
        vattiUpdateOrder.preUpdate();
        vattiUpdateOrderMapper.updateProcessFlag(vattiUpdateOrder);
    }
}
