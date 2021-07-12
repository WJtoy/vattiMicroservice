package com.kkl.kklplus.b2b.vatti.mapper;

import com.kkl.kklplus.b2b.vatti.entity.SysLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysLogMapper {

    Integer insert(SysLog sysLog);

    SysLog get(Long id);
}
