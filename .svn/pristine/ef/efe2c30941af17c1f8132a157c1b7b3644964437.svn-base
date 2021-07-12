package com.kkl.kklplus.b2b.vatti.mapper;


import com.github.pagehelper.Page;
import com.kkl.kklplus.b2b.vatti.entity.VattiOrderInfo;
import com.kkl.kklplus.entity.b2bcenter.sd.B2BOrderSearchModel;
import com.kkl.kklplus.entity.b2bcenter.sd.B2BOrderTransferResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VattiOrderInfoMapper {

    /**
     * 查询是否存在该工单
     * @param guid
     * @return
     */
    Long findOrderByGuid(@Param("guid")String guid);

    void insertOrderInfo(VattiOrderInfo vattiOrderInfo);

    /**
     * 批量插
     * @param list
     */
    void insertBatchOrderInfo(List<VattiOrderInfo> list);   //批量插

    VattiOrderInfo findOrderInfoByGuid(@Param("guid")String guid);

    List<VattiOrderInfo> findOrdersProcessFlag(@Param("orderNos") List<B2BOrderTransferResult> orderNos);

    void updateTransferResult(VattiOrderInfo wis);

    Page<VattiOrderInfo> getList(B2BOrderSearchModel workcardSearchModel);
}
