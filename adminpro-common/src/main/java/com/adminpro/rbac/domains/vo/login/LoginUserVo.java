package com.adminpro.rbac.domains.vo.login;

import com.adminpro.core.base.entity.BaseVO;
import lombok.Data;

@Data
public class LoginUserVo extends BaseVO {
    private String userId;

    private String password;

    private String platform;

    private String captcha;
}
