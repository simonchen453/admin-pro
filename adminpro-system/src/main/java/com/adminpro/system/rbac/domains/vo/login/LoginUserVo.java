package com.adminpro.system.rbac.domains.vo.login;

import com.adminpro.framework.base.entity.BaseVO;
import lombok.Data;

@Data
public class LoginUserVo extends BaseVO {
    private String userId;

    private String password;

    private String domain;

    private String captcha;
}
