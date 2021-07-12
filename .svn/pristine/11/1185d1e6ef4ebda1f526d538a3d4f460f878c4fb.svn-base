package com.kkl.kklplus.b2b.vatti.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.gson.Gson;
import com.kkl.kklplus.b2b.vatti.entity.SysLog;
import com.kkl.kklplus.b2b.vatti.mapper.B2BProcesslogMapper;
import com.kkl.kklplus.b2b.vatti.mapper.SysLogMapper;
import com.kkl.kklplus.b2b.vatti.utils.QuarterUtils;
import com.kkl.kklplus.entity.b2b.common.B2BProcessFlag;
import com.kkl.kklplus.entity.b2bcenter.rpt.B2BOrderProcesslog;
import com.kkl.kklplus.entity.b2bcenter.rpt.B2BProcessLogSearchModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Slf4j
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class B2BProcesslogService {

    @Resource
    private B2BProcesslogMapper b2BProcesslogMapper;

    @Resource
    private SysLogMapper sysLogMapper;

    /**
     * 添加原始数据
     * @param b2BProcesslog
     */
    public void insert(B2BOrderProcesslog b2BProcesslog){
        b2BProcesslogMapper.insert(b2BProcesslog);
    }


    public B2BOrderProcesslog savelog(String interfaceName,String json){
        B2BOrderProcesslog b2BProcesslog = new B2BOrderProcesslog();
        b2BProcesslog.setInterfaceName(interfaceName);
        b2BProcesslog.setProcessFlag(B2BProcessFlag.PROCESS_FLAG_FAILURE.value);
        b2BProcesslog.setProcessTime(0);
        b2BProcesslog.setCreateById(1L);
        b2BProcesslog.setUpdateById(1L);
        b2BProcesslog.preInsert();
        b2BProcesslog.setQuarter(QuarterUtils.getQuarter(b2BProcesslog.getCreateDate()));
        b2BProcesslog.setInfoJson(json);
        this.insert(b2BProcesslog);
        return b2BProcesslog;
    }

    public void updateProcessFlag(B2BOrderProcesslog b2BProcesslog) {
        try{
            b2BProcesslog.preUpdate();
            b2BProcesslog.setProcessComment(StringUtils.left(b2BProcesslog.getProcessComment(),200));
            b2BProcesslogMapper.updateProcessFlag(b2BProcesslog);
        }catch (Exception e) {
            log.error("原始数据结果修改错误", e.getMessage());
            SysLog sysLog = new SysLog();
            sysLog.setCreateDt(System.currentTimeMillis());
            sysLog.setType(1);
            sysLog.setCreateById(1L);
            sysLog.setParams(new Gson().toJson(b2BProcesslog));
            sysLog.setException( e.getMessage());
            sysLog.setTitle("原始数据结果修改错误");
            sysLog.setQuarter(QuarterUtils.getQuarter(sysLog.getCreateDt()));
            sysLogMapper.insert(sysLog);
        }
    }

    public Page<B2BOrderProcesslog> getList(B2BProcessLogSearchModel processLogSearchModel, String code) {
        if (processLogSearchModel.getPage() != null) {
            PageHelper.startPage(processLogSearchModel.getPage().getPageNo(), processLogSearchModel.getPage().getPageSize());
            return b2BProcesslogMapper.getList(processLogSearchModel,code);
        }
        else {
            return null;
        }
    }
}
