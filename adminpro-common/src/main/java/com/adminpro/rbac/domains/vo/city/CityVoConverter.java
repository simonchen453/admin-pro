package com.adminpro.rbac.domains.vo.city;

import com.adminpro.core.jdbc.query.IModelConverter;
import com.adminpro.rbac.domains.entity.city.CityEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/6.
 */
@Component
public class CityVoConverter implements IModelConverter<List<CityEntity>, List<CityVo>> {
    @Override
    public List<CityVo> convert(List<CityEntity> cityList) {
        List<CityVo> list = new ArrayList<>();
        if (cityList == null) {
            return list;
        }
        for (int i = 0; i < cityList.size(); i++) {
            CityEntity cityEntity = cityList.get(i);
            CityVo cityVo = new CityVo();
            cityVo.setId(cityEntity.getId());
            cityVo.setTitle(cityEntity.getTitle());
            cityVo.setParent(cityEntity.getParent());
            cityVo.setLevel(cityEntity.getLevel());
            cityVo.setKeyword(cityEntity.getKeyword());
            list.add(cityVo);
        }

        return list;
    }

    @Override
    public List<CityEntity> inverse(List<CityVo> voList) {
        List<CityEntity> list = new ArrayList<>();
        if (voList == null) {
            return null;
        }
        for (int i = 0; i < voList.size(); i++) {
            CityVo cityVo = voList.get(i);
            CityEntity cityEntity = new CityEntity();
            cityEntity.setId(cityVo.getId());
            cityEntity.setTitle(cityVo.getTitle());
            cityEntity.setParent(cityVo.getParent());
            cityEntity.setLevel(cityVo.getLevel());
            cityEntity.setKeyword(cityVo.getKeyword());
            list.add(cityEntity);
        }

        return list;
    }
}
