package com.adminpro.rbac.domains.vo.oss;

import com.adminpro.core.base.util.DateUtil;
import com.adminpro.core.jdbc.query.IModelConverter;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

@Component
public class ListOssVoConverter implements IModelConverter<ListOssDto, ListOssVo> {

    @Override
    public ListOssVo convert(ListOssDto entity) {
        if (entity == null) {
            return null;
        }
        ListOssVo listVo = new ListOssVo();
        listVo.setOriginal(entity.getOriginal());
        listVo.setId(entity.getId());
        listVo.setUrl(entity.getUrl());
        listVo.setSize(FileUtils.byteCountToDisplaySize(entity.getSize()));
        listVo.setCreatedDate(DateUtil.formatDateTime(entity.getCreatedDate()));
        listVo.setCreatedBy(entity.getCreatedBy());
        listVo.setKey(entity.getKey());
        listVo.setHash(entity.getHash());
        listVo.setType(entity.getType());
        return listVo;
    }

    @Override
    public ListOssDto inverse(ListOssVo s) {
        return null;
    }

}
