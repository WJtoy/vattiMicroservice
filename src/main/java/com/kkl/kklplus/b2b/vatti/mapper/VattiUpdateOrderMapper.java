package com.kkl.kklplus.b2b.vatti.mapper;

import com.kkl.kklplus.b2b.vatti.entity.VattiUpdateOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface VattiUpdateOrderMapper {

    void insert(VattiUpdateOrder vattiUpdateOrder);

    void updateProcessFlag(VattiUpdateOrder vattiUpdateOrder);

    Integer findAppointmentCount(@Param("guid") String guid);
}
