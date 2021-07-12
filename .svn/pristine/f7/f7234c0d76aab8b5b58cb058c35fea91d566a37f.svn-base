package com.kkl.kklplus.b2b.vatti.service;

import com.kkl.kklplus.b2b.vatti.entity.VattiArea;
import com.kkl.kklplus.b2b.vatti.mapper.VattiAreaMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class VattiAreaService {

    @Resource
    private VattiAreaMapper vattiAreaMapper;

    public VattiArea insert(Long parentId, String parentIds, String itemCode, String itemName, String fullName, int type, long createAt) {
        Long id = vattiAreaMapper.findIdByCode(itemCode);
        VattiArea area = new VattiArea();
        area.setParentId(parentId);
        area.setParentIds(parentIds);
        area.setCode(itemCode);
        area.setName(itemName);
        area.setFullName(fullName);
        area.setType(type);
        area.setCreateAt(createAt);
        if(id == null) {
            vattiAreaMapper.insert(area);
        }else{
            area.setId(id);
            vattiAreaMapper.update(area);
        }
        return area;
    }

    public List<VattiArea> findByCode(String regionNo, String cityNo, String districtNo) {
        return vattiAreaMapper.findByCode(regionNo,cityNo,districtNo);
    }
}
