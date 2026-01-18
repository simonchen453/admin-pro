package com.adminpro.system.rbac.api;

import com.adminpro.system.rbac.common.RbacConstants;
import org.apache.commons.lang3.StringUtils;

public class DomainHelper {
    /**
     * 判断是否为互联网用户
     *
     * @param userDomain 用户域
     * @return true表示是互联网用户
     */
    public boolean isInternetUser(String userDomain) {
        return StringUtils.equals(userDomain, RbacConstants.INTERNET_DOMAIN);
    }

    /**
     * 判断是否为系统用户
     *
     * @param userDomain 用户域
     * @return true表示是系统用户
     */
    public boolean isSystemUser(String userDomain) {
        return StringUtils.equals(userDomain, RbacConstants.SYSTEM_DOMAIN);
    }

    /**
     * 判断是否为内网用户
     *
     * @param userDomain 用户域
     * @return true表示是内网用户
     */
    public boolean isIntranetUser(String userDomain) {
        return StringUtils.equals(userDomain, RbacConstants.INTRANET_DOMAIN);
    }
}
