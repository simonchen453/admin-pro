package com.adminpro.system.rbac.domains.vo.apk;

import com.adminpro.framework.base.util.FileUtil;
import com.adminpro.framework.jdbc.query.IModelConverter;
import com.adminpro.system.core.common.constants.WebConstants;
import com.adminpro.system.core.common.helper.WebHelper;
import com.adminpro.system.rbac.domains.entity.apk.APKEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

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
        aPKVo.setDownloadUrl(WebConstants.getServerAddress() + request.getContextPath() + FileUtil.FILE_URL_PREFIX + apk.getDownloadUrl());
        aPKVo.setMessage(apk.getMessage());
        return aPKVo;
    }

    @Override
    public APKEntity inverse(APKVO s) {
        return null;
    }
}
