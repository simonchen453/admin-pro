package com.adminpro.rbac.domains.vo.apk;

import com.adminpro.core.jdbc.query.IModelConverter;
import com.adminpro.framework.common.constants.WebConstants;
import com.adminpro.framework.common.helper.WebHelper;
import com.adminpro.rbac.domains.entity.apk.APKEntity;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class APKVoConverter implements IModelConverter<APKEntity, APKVO> {
    public APKVO convert(APKEntity apk) {
        if (apk == null) {
            return null;
        }
        APKVO aPKVo = new APKVO();
        aPKVo.setId(apk.getId());
        aPKVo.setType(apk.getType());
        aPKVo.setForceUpdate(apk.isForceUpdate());
        aPKVo.setVerName(apk.getVerName());
        aPKVo.setVerCode(apk.getVerCode());
        aPKVo.setOsVersion(apk.getOsVersion());
        HttpServletRequest request = WebHelper.getHttpRequest();
        aPKVo.setDownloadUrl(WebConstants.getServerAddress() + request.getContextPath() + "/upload/" + apk.getDownloadUrl());
        aPKVo.setMessage(apk.getMessage());
        return aPKVo;
    }

    @Override
    public APKEntity inverse(APKVO s) {
        return null;
    }
}
