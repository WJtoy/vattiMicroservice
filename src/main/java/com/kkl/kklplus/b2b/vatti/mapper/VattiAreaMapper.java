package com.kkl.kklplus.b2b.vatti.mapper;

import com.kkl.kklplus.b2b.vatti.entity.VattiArea;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VattiAreaMapper {

    Integer insert(VattiArea area);

    List<VattiArea> findByCode(@Param("regionNo") String regionNo, @Param("cityNo") String cityNo, @Param("districtNo") String districtNo);

    Long findIdByCode(@Param("code") String itemCode);

    Integer update(VattiArea area);
}
